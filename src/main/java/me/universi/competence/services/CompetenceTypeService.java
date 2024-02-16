package me.universi.competence.services;

import me.universi.Sys;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CompetenceTypeService {
    private final CompetenceTypeRepository competenceTypeRepository;

    public CompetenceTypeService(CompetenceTypeRepository competenceTypeRepository) {
        this.competenceTypeRepository = competenceTypeRepository;
    }

    public static CompetenceTypeService getInstance() {
        return Sys.context.getBean("competenceTypeService", CompetenceTypeService.class);
    }

    public CompetenceType findFirstById(UUID id) {
        CompetenceType competenceType = competenceTypeRepository.findFirstById(id).orElse(null);

        if(hasAccessToCompetenceType(competenceType)){
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

    public CompetenceType findFirstByName(String name) {
        CompetenceType competenceType = findFirstByNameIgnoringAccess(name);

        if (hasAccessToCompetenceType(competenceType)) {
            return competenceType;
        }else{
            return null;
        }
    }

    private CompetenceType findFirstByNameIgnoringAccess(String name) {
        return competenceTypeRepository.findFirstByName(name).orElse(null);
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

    public boolean hasAccessToCompetenceType(CompetenceType competence) {
        if (competence == null) return false;

        UserService userService = UserService.getInstance();
        var currentUser = userService.getUserInSession();

        return competence.isReviewed()
            || userService.isUserAdmin(currentUser)
            || competence.getProfilesWithAccess()
                .stream()
                .anyMatch(p -> p.getId().equals(currentUser.getProfile().getId()));
    }
}
