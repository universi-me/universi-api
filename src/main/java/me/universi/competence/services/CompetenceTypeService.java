package me.universi.competence.services;

import java.util.*;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.api.interfaces.UniqueNameEntityService;
import me.universi.competence.dto.CreateCompetenceTypeDTO;
import me.universi.competence.dto.MergeCompetenceTypeDTO;
import me.universi.competence.dto.UpdateCompetenceTypeDTO;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.competence.repositories.CompetenceTypeRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

@Service
public class CompetenceTypeService extends UniqueNameEntityService<CompetenceType> {
    private final CompetenceTypeRepository competenceTypeRepository;
    private final CompetenceRepository competenceRepository;
    private final ProfileService profileService;
    private final UserService userService;

    public CompetenceTypeService(CompetenceTypeRepository competenceTypeRepository, CompetenceRepository competenceRepository, ProfileService profileService, UserService userService) {
        this.competenceTypeRepository = competenceTypeRepository;
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;

        this.entityName = "Tipo de Competência";
    }

    public static CompetenceTypeService getInstance() {
        return Sys.context.getBean("competenceTypeService", CompetenceTypeService.class);
    }

    private Optional<CompetenceType> findIgnoringAccess( String idOrName ) {
        return competenceTypeRepository.findFirstByIdOrNameIgnoringCase(
            CastingUtil.getUUID( idOrName ).orElse( null ),
            idOrName
        );
    }

    private Optional<CompetenceType> findIgnoringAccess( UUID id ) {
        return competenceTypeRepository.findById( id );
    }

    @Override
    public Optional<CompetenceType> findUnchecked( UUID id ) {
        return findIgnoringAccess( id ).map( this::echoIfHasAccess );
    }

    @Override
    public Optional<CompetenceType> findByNameUnchecked( String name ) {
        return competenceTypeRepository.findFirstByNameIgnoreCase( name ).map( this::echoIfHasAccess );
    }

    @Override
    public Optional<CompetenceType> findByIdOrNameUnchecked( String idOrName ) {
        return findIgnoringAccess( idOrName ).map( this::echoIfHasAccess );
    }

    public void delete( String id ) throws AccessDeniedException {
        var ct = findByIdOrNameOrThrow( id );
        checkPermissionToDelete( ct );
        competenceTypeRepository.delete( ct );
    }

    @Override
    public List<CompetenceType> findAllUnchecked() {
        return competenceTypeRepository.findAll()
            .stream()
            .filter(this::hasAccessToCompetenceType)
            .toList();
    }

    public CompetenceType update( String id, UpdateCompetenceTypeDTO updateCompetenceTypeDTO ) throws UniversiForbiddenAccessException, UniversiConflictingOperationException {
        var existingCompetenceType = findByIdOrNameOrThrow( id );
        checkPermissionToEdit( existingCompetenceType );

        if ( updateCompetenceTypeDTO.name() != null ) {
            var competenceWithName = findIgnoringAccess( updateCompetenceTypeDTO.name() );

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

            existingCompetenceType.setProfilesWithAccess( new ArrayList<>() );
        }

        return competenceTypeRepository.saveAndFlush( existingCompetenceType );
    }

    public CompetenceType create( @NotNull CreateCompetenceTypeDTO createCompetenceTypeDTO ) throws UniversiConflictingOperationException {
        var profileInSession = profileService.getProfileInSessionOrThrow();
        var existingCompetenceType = findIgnoringAccess( createCompetenceTypeDTO.name() );

        if ( existingCompetenceType.isEmpty() ) {
            var ct = new CompetenceType();
            ct.setName( createCompetenceTypeDTO.name() );
            ct.setReviewed( false );
            ct.setProfilesWithAccess( new ArrayList<>(
                    Arrays.asList( profileInSession )
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
            throw makeDeniedException( "unir" );

        var removedCompetenceType = findOrThrow( mergeCompetenceTypeDTO.removedCompetenceType() );
        var remainingCompetenceType = findOrThrow( mergeCompetenceTypeDTO.remainingCompetenceType() );

        var updateCompetences = competenceRepository.findAll().stream()
            .filter( c -> c.getCompetenceType().getId().equals( removedCompetenceType.getId() ) )
            .toList();

        updateCompetences.forEach(c -> c.setCompetenceType( remainingCompetenceType ));
        competenceRepository.saveAll(updateCompetences);

        removedCompetenceType.setProfilesWithAccess( new ArrayList<>() );

        removedCompetenceType.setDeleted( true );
        competenceTypeRepository.save( removedCompetenceType );
    }

    private @Nullable CompetenceType echoIfHasAccess( CompetenceType ct ) {
        return ct != null && hasAccessToCompetenceType( ct )
            ? ct
            : null;
    }

    public boolean hasAccessToCompetenceType(CompetenceType competence) {
        var profileInSession = profileService.getProfileInSession();
        if ( profileInSession.isEmpty() )
            return competence.isReviewed();

        return hasAccessToCompetenceType( competence, profileInSession.get() );
    }

    public boolean hasAccessToCompetenceType(@NotNull CompetenceType competence, @NotNull Profile profile) {
        return competence.isReviewed()
            || userService.isUserAdmin( profile.getUser() )
            || competence.getProfilesWithAccess()
                .stream()
                .anyMatch(p -> p.getId().equals(profile.getId()));
    }

    public CompetenceType grantAccessToProfile(@NotNull CompetenceType competenceType, @NotNull Profile profile) {
        competenceType.addProfileWithAccess( profile );
        return competenceTypeRepository.saveAndFlush( competenceType );
    }

    @Override
    public boolean isValid( CompetenceType competenceType ) {
        return competenceType != null;
    }

    @Override
    public boolean hasPermissionToEdit( CompetenceType ct ) {
        return userService.isUserAdminSession();
    }

    @Override
    public boolean hasPermissionToDelete( CompetenceType ct ) {
        return hasPermissionToEdit( ct );
    }
}
