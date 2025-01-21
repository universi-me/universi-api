package me.universi.competence.services;

import me.universi.Sys;
import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.dto.UpdateCompetenceDTO;
import me.universi.competence.entities.Competence;
import me.universi.competence.repositories.CompetenceRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompetenceService {
    private final CompetenceRepository competenceRepository;
    private final ProfileService profileService;
    private final UserService userService;
    private final CompetenceTypeService competenceTypeService;

    public CompetenceService(CompetenceRepository competenceRepository, ProfileService profileService, UserService userService, CompetenceTypeService competenceTypeService){
        this.competenceRepository = competenceRepository;
        this.profileService = profileService;
        this.userService = userService;
        this.competenceTypeService = competenceTypeService;
    }

    public static CompetenceService getInstance() {
        return Sys.context.getBean("competenceService", CompetenceService.class);
    }

    public Optional<Competence> find( UUID id ) {
        return competenceRepository.findById( id );
    }

    public Competence findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Competência de ID '" + id + "' não encontrada" ) );
    }

    public List<Competence> findByProfileId( UUID profileId ) {
        return competenceRepository.findByProfileId( profileId );
    }

    public List<Competence> findByProfileIdAndCompetenceTypeId( UUID profileId, UUID competenceTypeId ) {
        return competenceRepository.findByProfileIdAndCompetenceTypeId( profileId, competenceTypeId );
    }

    public List<Competence> findAll() {
        return competenceRepository.findAll();
    }

    public Competence create( @NotNull CreateCompetenceDTO createCompetenceDTO, @NotNull Profile profile ) {
        var competenceType = competenceTypeService.findOrThrow( createCompetenceDTO.competenceTypeId() );

        return competenceRepository.saveAndFlush( new Competence(
            competenceType,
            createCompetenceDTO.description(),
            createCompetenceDTO.level(),
            profile
        ) );
    }

    public Competence create( @NotNull CreateCompetenceDTO createCompetenceDTO ) {
        return create( createCompetenceDTO, profileService.getProfileInSessionOrThrow() );
    }

    public Competence update( UUID id, UpdateCompetenceDTO updateCompetenceDTO ) {
        var competence = findOrThrow( id );
        checkPermissionForEdit( competence );

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
        checkPermissionForDelete( competence );
        competenceRepository.delete( competence );
    }

    private void checkPermissionForEdit( @NotNull Competence competence ) throws AccessDeniedException {
        if ( userService.isUserAdminSession() )
            return;

        var profile = profileService.getProfileInSession();

        if ( profile.isEmpty() || !competence.getProfile().getId().equals( profile.get().getId() ) )
            throw new AccessDeniedException( "Você não tem permissão para alterar esta Competência" );
    }

    private void checkPermissionForDelete( @NotNull Competence competence ) throws AccessDeniedException {
        var profile = profileService.getProfileInSession();

        if ( profile.isEmpty() || (
            !competence.getProfile().getId().equals( profile.get().getId() )
            && !userService.isUserAdminSession()
        ) )
            throw new AccessDeniedException( "Você não tem permissão para deletar esta Competência" );
    }

    public boolean validate( Competence competence ) {
        return competence != null
            && competenceTypeService.validate( competence.getCompetenceType() );
    }
}
