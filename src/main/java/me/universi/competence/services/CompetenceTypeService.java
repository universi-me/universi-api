package me.universi.competence.services;

import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.competence.dto.CreateCompetenceTypeDTO;
import me.universi.competence.dto.MergeCompetenceTypeDTO;
import me.universi.competence.dto.UpdateCompetenceTypeDTO;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.exceptions.CompetenceException;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetenceTypeService {
    private final CompetenceTypeRepository competenceTypeRepository;
    private final CompetenceRepository competenceRepository;
    private final ProfileService profileService;
    private final UserService userService;

    public CompetenceTypeService(CompetenceTypeRepository competenceTypeRepository, CompetenceRepository competenceRepository, ProfileService profileService, UserService userService) {
        this.competenceTypeRepository = competenceTypeRepository;
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;
    }

    public static CompetenceTypeService getInstance() {
        return Sys.context.getBean("competenceTypeService", CompetenceTypeService.class);
    }

    private Optional<CompetenceType> findIgnoringAccess( UUID id ) {
        return competenceTypeRepository.findById( id );
    }

    private CompetenceType findIgnoringAccessOrThrow( UUID id ) throws EntityNotFoundException {
        return findIgnoringAccess( id ).orElseThrow( () -> new EntityNotFoundException( "Tipo de Competência de ID '" + id + "' não encontrado" ) );
    }

    public Optional<CompetenceType> find( UUID id ) {
        var ct = findIgnoringAccess( id );

        return hasAccessToCompetenceType( ct )
            ? ct
            : Optional.empty();
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

    private Optional<CompetenceType> findByNameIgnoringAccess( String name ) {
        return competenceTypeRepository.findFirstByNameIgnoreCase(name);
    }

    public Optional<CompetenceType> findByName( String name ) {
        var ct = findByNameIgnoringAccess( name );
        return hasAccessToCompetenceType( ct )
            ? ct
            : Optional.empty();
    }

    public void delete( UUID id ) throws AccessDeniedException {
        if ( !userService.isUserAdminSession() )
            throw new AccessDeniedException( "Esta operação não é permitida para este usuário." );

        var ct = findIgnoringAccessOrThrow( id );
        competenceTypeRepository.delete( ct );
    }

    public List<CompetenceType> findAll() {
        return competenceTypeRepository.findAll()
            .stream()
            .filter(this::hasAccessToCompetenceType)
            .toList();
    }

    public CompetenceType update( UUID id, UpdateCompetenceTypeDTO updateCompetenceTypeDTO ) throws UniversiForbiddenAccessException, UniversiConflictingOperationException {
        if ( !userService.isUserAdminSession() )
            throw new UniversiForbiddenAccessException( "Esta operação não é permitida para este usuário." );

        var existingCompetenceType = findOrThrow( id );

        if ( updateCompetenceTypeDTO.name() != null ) {
            var competenceWithName = findByNameIgnoringAccess( updateCompetenceTypeDTO.name() );

            if ( competenceWithName.isPresent()
                && !competenceWithName.get().getId().equals(existingCompetenceType.getId())
            ) {
                throw new UniversiConflictingOperationException( "Já existe um tipo de competência com este nome" );
            }

            existingCompetenceType.setName( updateCompetenceTypeDTO.name() );
        }

        if ( updateCompetenceTypeDTO.reviewed() != null
            && !existingCompetenceType.isReviewed()
        ) {
            existingCompetenceType.setReviewed( updateCompetenceTypeDTO.reviewed() );

            existingCompetenceType.setProfilesWithAccess( Arrays.asList() );
        }

        return competenceTypeRepository.saveAndFlush( existingCompetenceType );
    }

    public CompetenceType create( @NotNull CreateCompetenceTypeDTO createCompetenceTypeDTO ) throws UniversiConflictingOperationException {
        var profileInSession = profileService.getProfileInSessionOrThrow();
        var existingCompetenceType = findByNameIgnoringAccess( createCompetenceTypeDTO.name() );

        if ( existingCompetenceType.isEmpty() ) {
            var ct = new CompetenceType();
            ct.setName( createCompetenceTypeDTO.name() );
            ct.setReviewed( false );
            ct.setProfilesWithAccess( Arrays.asList(
                profileInSession
            ) );

            return competenceTypeRepository.saveAndFlush( ct );
        }

        var competenceType = existingCompetenceType.get();

        if ( !hasAccessToCompetenceType( competenceType ) ) {
            competenceType.addProfileWithAccess( profileInSession );
            return competenceTypeRepository.saveAndFlush( competenceType );
        }

        throw new UniversiConflictingOperationException( "Este tipo de competência já existe" );
    }

    public void merge( MergeCompetenceTypeDTO mergeCompetenceTypeDTO ) throws AccessDeniedException {
        if ( !userService.isUserAdminSession() )
            throw new CompetenceException("Esta operação não é permitida para este usuário.");

        var removedCompetenceType = findOrThrow( mergeCompetenceTypeDTO.removedCompetenceType() );
        var remainingCompetenceType = findOrThrow( mergeCompetenceTypeDTO.remainingCompetenceType() );

        var updateCompetences = competenceRepository.findAll().stream()
            .filter( c -> c.getCompetenceType().getId().equals( removedCompetenceType.getId() ) )
            .toList();

        updateCompetences.forEach(c -> c.setCompetenceType( remainingCompetenceType ));
        competenceRepository.saveAll(updateCompetences);

        removedCompetenceType.setProfilesWithAccess( Arrays.asList() );
        competenceTypeRepository.delete( removedCompetenceType );
    }

    private boolean hasAccessToCompetenceType( Optional<CompetenceType> competenceType ) {
        return competenceType.isPresent() && hasAccessToCompetenceType( competenceType.get() );
    }

    public boolean hasAccessToCompetenceType(CompetenceType competence) {
        var profileInSession = profileService.getProfileInSession();
        if ( profileInSession.isEmpty() )
            return competence.isReviewed();

        return hasAccessToCompetenceType( competence, profileInSession.get() );
    }

    public boolean hasAccessToCompetenceType(@NotNull CompetenceType competence, @NotNull Profile profile) {
        var currentUser = profile.getUser();

        return competence.isReviewed()
            || UserService.getInstance().isUserAdmin(currentUser)
            || competence.getProfilesWithAccess()
                .stream()
                .anyMatch(p -> p.getId().equals(profile.getId()));
    }

    public CompetenceType grantAccessToProfile(@NotNull CompetenceType competenceType, @NotNull Profile profile) {
        competenceType.addProfileWithAccess( profile );
        return competenceTypeRepository.saveAndFlush( competenceType );
    }

    public boolean validate( CompetenceType competenceType ) {
        return competenceType != null;
    }
}
