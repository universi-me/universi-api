package me.universi.competence.services;

import me.universi.Sys;
import me.universi.api.interfaces.EntityService;
import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.dto.UpdateCompetenceDTO;
import me.universi.competence.entities.Competence;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetenceService extends EntityService<Competence> {
    private final CompetenceRepository competenceRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final CompetenceTypeService competenceTypeService;

    public CompetenceService(CompetenceRepository competenceRepository, ProfileService profileService, UserService userService, CompetenceTypeService competenceTypeService){
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceTypeService = competenceTypeService;

        this.entityName = "Competência";
    }

    public static CompetenceService getInstance() {
        return Sys.context.getBean("competenceService", CompetenceService.class);
    }

    public Optional<Competence> find( UUID id ) {
        return competenceRepository.findById( id );
    }

    public List<Competence> findByProfile( String profileIdOrUsername ) {
        var profile = profileService.findByIdOrUsernameOrThrow( profileIdOrUsername );
        return competenceRepository.findByProfileId( profile.getId() );
    }

    public List<Competence> findByProfile( UUID profileId ) {
        return competenceRepository.findByProfileId( profileId );
    }

    public List<Competence> findByProfileAndCompetenceType( String profileIdOrUsername, String competenceTypeIdOrName ) {
        var profile = profileService.findByIdOrUsernameOrThrow( profileIdOrUsername );
        var competenceType = competenceTypeService.findByIdOrNameOrThrow( competenceTypeIdOrName );

        return competenceRepository.findByProfileIdAndCompetenceTypeId( profile.getId(), competenceType.getId() );
    }

    public List<Competence> findByProfileAndCompetenceType( UUID profileId, UUID competenceTypeId ) {
        return competenceRepository.findByProfileIdAndCompetenceTypeId( profileId, competenceTypeId );
    }

    public List<Competence> findAll() {
        return competenceRepository.findAll();
    }

    public Competence create( @NotNull CreateCompetenceDTO createCompetenceDTO, @NotNull Profile profile ) {
        var competenceType = competenceTypeService.findOrThrow( createCompetenceDTO.competenceTypeId() );

        return competenceRepository.saveAndFlush( new Competence(
            competenceType,
            "",
            createCompetenceDTO.level(),
            profile
        ) );
    }

    public Competence create( @NotNull CreateCompetenceDTO createCompetenceDTO ) {
        return create( createCompetenceDTO, profileService.getProfileInSessionOrThrow() );
    }

    public Competence update( UUID id, UpdateCompetenceDTO updateCompetenceDTO ) {
        var competence = findOrThrow( id );
        checkPermissionToEdit( competence );

        if ( updateCompetenceDTO.competenceTypeId() != null )
            competence.setCompetenceType(
                competenceTypeService.findOrThrow( updateCompetenceDTO.competenceTypeId() )
            );

        if ( updateCompetenceDTO.description() != null && !updateCompetenceDTO.description().isBlank() )
            competence.setDescription( updateCompetenceDTO.description() );

        if ( updateCompetenceDTO.level() != null )
            competence.setLevel( updateCompetenceDTO.level() );

        return competenceRepository.save( competence );
    }

    public void delete( UUID id ) {
        var competence = findOrThrow( id );
        checkPermissionToDelete( competence );

        competence.setDeleted( true );
        competenceRepository.save( competence );
    }

    @Override
    public boolean hasPermissionToEdit( @NotNull Competence competence ) {
        if ( userService.isUserAdminSession() )
            return true;

        var profile = profileService.getProfileInSession();

        return profile.isPresent() && competence.getProfile().getId().equals( profile.get().getId() );
    }

    @Override
    public boolean hasPermissionToDelete( @NotNull Competence competence ) throws AccessDeniedException {
        return hasPermissionToEdit( competence );
    }

    public boolean validate( Competence competence ) {
        return competence != null
            && competenceTypeService.validate( competence.getCompetenceType() );
    }
}
