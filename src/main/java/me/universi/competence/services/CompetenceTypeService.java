package me.universi.competence.services;

import me.universi.Sys;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetenceTypeService {
    private final CompetenceTypeRepository competenceTypeRepository;
    private final CompetenceRepository competenceRepository;

    public CompetenceTypeService(CompetenceTypeRepository competenceTypeRepository, CompetenceRepository competenceRepository) {
        this.competenceTypeRepository = competenceTypeRepository;
        this.competenceRepository = competenceRepository;
    }

    public static CompetenceTypeService getInstance() {
        return Sys.context.getBean("competenceTypeService", CompetenceTypeService.class);
    }

    public Optional<CompetenceType> find( UUID id ) {
        return competenceTypeRepository.findById( id );
    }

    public List<Optional<CompetenceType>> find( Collection<UUID> id ) {
        return id.stream().map( this::find ).toList();
    }

    public CompetenceType findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Competência de ID '" + id + "' não encontrado" ) );
    }

    public List<CompetenceType> findOrThrow( Collection<UUID> id ) {
        return id.stream().map( this::findOrThrow ).toList();
    }

    public CompetenceType findFirstById(UUID id) {
        CompetenceType competenceType = competenceTypeRepository.findFirstById(id).orElse(null);

        if(competenceType != null && hasAccessToCompetenceType(competenceType)){
            return competenceType;
        }else{
            return null;
        }
    }

    public CompetenceType findFirstById(String id) {
        try {
            return findFirstById(UUID.fromString(id));
        } catch (IllegalArgumentException invalidUUID) {
            return null;
        }
    }

    public List<CompetenceType> findAllById(@NotNull @NotNull Collection<@NotNull UUID> ids) {
        return new ArrayList<>(ids
            .stream()
            .map(this::findFirstById)
            .filter(Objects::nonNull)
            .toList()
        );
    }

    public CompetenceType findFirstByName(String name) {
        CompetenceType competenceType = findFirstByNameIgnoringAccess(name);

        if (hasAccessToCompetenceType(competenceType)) {
            return competenceType;
        }else{
            return null;
        }
    }

    private CompetenceType findFirstByNameIgnoringAccess(String name) {
        return competenceTypeRepository.findFirstByNameIgnoreCase(name).orElse(null);
    }

    private CompetenceType save(CompetenceType competenceType) {
        return competenceTypeRepository.saveAndFlush(competenceType);
    }

    public void delete(UUID id) throws CompetenceException {
        delete(findFirstById(id));
    }

    public void delete(CompetenceType competenceType) throws CompetenceException {
        if (!UserService.getInstance().isUserAdminSession()) {
            throw new CompetenceException("Esta operação não é permitida para este usuário.");
        }

        if (competenceType == null) {
            throw new CompetenceException("Competência não encontrada.");
        }

        competenceType.setDeleted(true);
        save(competenceType);
    }

    public List<CompetenceType> findAll() {
        return competenceTypeRepository.findAll()
            .stream()
            .filter(this::hasAccessToCompetenceType)
            .toList();
    }

    public CompetenceType update(CompetenceType competenceType) throws CompetenceException {
        return this.update(competenceType, competenceType.getId());
    }

    public CompetenceType update(CompetenceType updateTo, UUID id) throws CompetenceException {
        if (!UserService.getInstance().isUserAdminSession()) {
            throw new CompetenceException("Esta operação não é permitida para este usuário.");
        }

        var existingCompetenceType = competenceTypeRepository.findById(id)
            .orElseThrow(() -> new CompetenceException("Tipo de competência não encontrado."));

        if (updateTo.getName() != null) {
            var competenceWithName = findFirstByNameIgnoringAccess(updateTo.getName());

            if (competenceWithName != null && !competenceWithName.getId().equals(existingCompetenceType.getId())) {
                throw new CompetenceException("Já existe um tipo de competência já existe.");
            }

            existingCompetenceType.setName(updateTo.getName());
        }

        if (!existingCompetenceType.isReviewed() && updateTo.isReviewed()) {
            existingCompetenceType.setReviewed(updateTo.isReviewed());

            // Delete the profiles with access now that every profile has access to it
            existingCompetenceType.setProfilesWithAccess(Arrays.asList());
        }

        return save(existingCompetenceType);
    }

    public CompetenceType create(CompetenceType competenceType) {
        CompetenceType existingCompetenceType = findFirstByNameIgnoringAccess(competenceType.getName());

        if(existingCompetenceType != null) {
            if (!hasAccessToCompetenceType(existingCompetenceType)) {
                existingCompetenceType.addProfileWithAccess(ProfileService.getInstance().getProfileInSession());
            }
        } else {
            existingCompetenceType = new CompetenceType();
            existingCompetenceType.setName(competenceType.getName());
            existingCompetenceType.setReviewed(false);

            existingCompetenceType.setProfilesWithAccess(Arrays.asList(
                ProfileService.getInstance().getProfileInSession()
            ));
        }

        return save(existingCompetenceType);
    }

    public void merge(CompetenceType removedTypeCompetence, CompetenceType remainingCompetenceType) throws CompetenceException {
        if (!UserService.getInstance().isUserAdminSession()) {
            throw new CompetenceException("Esta operação não é permitida para este usuário.");
        }

        if (removedTypeCompetence == null || remainingCompetenceType == null) {
            throw new CompetenceException("Tipo de competência não encontrado.");
        }

        var updateCompetences = competenceRepository.findAll().stream()
            .filter(c -> c.getCompetenceType().getId().equals(removedTypeCompetence.getId()))
            .toList();

        updateCompetences.forEach(c -> c.setCompetenceType(remainingCompetenceType));
        competenceRepository.saveAll(updateCompetences);

        removedTypeCompetence.setProfilesWithAccess(Arrays.asList());
        competenceTypeRepository.delete(removedTypeCompetence);
    }

    public boolean hasAccessToCompetenceType(CompetenceType competence) {
        return hasAccessToCompetenceType(competence, UserService.getInstance().getUserInSession().getProfile());
    }

    public boolean hasAccessToCompetenceType(@NotNull CompetenceType competence, @NotNull Profile profile) {
        var currentUser = profile.getUser();

        return competence.isReviewed()
            || UserService.getInstance().isUserAdmin(currentUser)
            || competence.getProfilesWithAccess()
                .stream()
                .anyMatch(p -> p.getId().equals(profile.getId()));
    }

    public void grantAccessToProfile(@NotNull CompetenceType competenceType, @NotNull Profile profile) {
        competenceType.getProfilesWithAccess().add(profile);
        save(competenceType);
    }

    public boolean validate( CompetenceType competenceType ) {
        return competenceType != null;
    }
}
