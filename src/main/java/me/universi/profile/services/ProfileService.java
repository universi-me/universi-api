package me.universi.profile.services;

import java.util.*;

import me.universi.user.services.AccountService;
import me.universi.user.services.LoginService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.api.interfaces.EntityService;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.entidades.FolderFavorite;
import me.universi.capacity.service.FolderService;
import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.entities.Competence;
import me.universi.competence.services.CompetenceService;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.education.entities.Education;
import me.universi.experience.entities.Experience;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.image.services.ImageMetadataService;
import me.universi.link.entities.Link;
import me.universi.profile.dto.UpdateProfileDTO;
import me.universi.profile.entities.Profile;
import me.universi.profile.repositories.PerfilRepository;
import me.universi.user.services.UserService;
import me.universi.util.CastingUtil;

@Service
public class ProfileService extends EntityService<Profile> {
    private final PerfilRepository perfilRepository;
    private final UserService userService;

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_BIO_LENGTH = 140;
    private final AccountService accountService;
    private final LoginService loginService;

    public ProfileService(PerfilRepository perfilRepository, UserService userService, AccountService accountService, LoginService loginService) {
        this.perfilRepository = perfilRepository;
        this.userService = userService;

        this.entityName = "Perfil";
        this.accountService = accountService;
        this.loginService = loginService;
    }

    public static ProfileService getInstance() {
        return Sys.context().getBean("profileService", ProfileService.class);
    }

    @Override
    public Optional<Profile> findUnchecked( UUID id ) {
        return perfilRepository.findById( id );
    }

    public Optional<Profile> findByIdOrUsername( String idOrUsername ) {
        return perfilRepository.findByIdOrUsername( CastingUtil.getUUID( idOrUsername ).orElse( null ), idOrUsername );
    }

    public @NotNull Profile findByIdOrUsernameOrThrow( String idOrUsername ) {
        return findByIdOrUsername( idOrUsername )
            .orElseThrow( () -> makeNotFoundException( "ID ou username", idOrUsername ) );
    }

    public List<Optional<Profile>> findByIdOrUsername( List<String> idsOrUsernames ) {
        return idsOrUsernames.stream().map( this::findByIdOrUsername ).toList();
    }

    public List<@NotNull Profile> findByIdOrUsernameOrThrow( List<String> idsOrUsernames ) {
        return idsOrUsernames.stream().map( this::findByIdOrUsernameOrThrow ).toList();
    }

    @Override
    public List<Profile> findAllUnchecked() {
        return perfilRepository.findAll();
    }

    public @NotNull Profile update( @NotNull UpdateProfileDTO dto ) {
        var myself = getProfileInSessionOrThrow();
        accountService.checkPasswordInSession( dto.password() );

        dto.firstname().ifPresent( firstname -> {
            firstname = validateFirstname( firstname );
            myself.setFirstname( firstname );
        } );

        dto.lastname().ifPresent( lastname -> {
            lastname = validateLastname( lastname );
            myself.setLastname( lastname );
        } );

        dto.image().ifPresent( imageId -> {
            var image = ImageMetadataService.getInstance().findOrThrow( imageId );
            myself.setImage( image );
        } );

        dto.biography().ifPresent( biography -> {
            biography = validateBiography( biography );
            myself.setBio( biography );
        } );

        dto.gender().ifPresent( myself::setGender );

        dto.department().ifPresent( id -> {
            // unset department if blank id was sent
            if ( id.isBlank() )
                myself.setDepartment( null );
            else
                myself.setDepartment( DepartmentService.getInstance().findByIdOrNameOrThrow( id ) );
        } );

        return perfilRepository.saveAndFlush( myself );
    }

    public void delete( @NotNull String idOrUsername ) {
        var profile = findByIdOrUsernameOrThrow( idOrUsername );
        checkPermissionToDelete( profile );

        profile.setDeleted( true );
        perfilRepository.save( profile );
    }

    public void grantCompetenceBadge( @NotNull Collection<@NotNull Folder> folders, @NotNull Profile profile ) {
        var folderService = FolderService.getInstance();
        var competenceService = CompetenceService.getInstance();
        var competenceTypeService = CompetenceTypeService.getInstance();

        for ( var f : folders ) {
            if ( !folderService.isComplete( profile, f ) )
                continue;

            for ( var competenceType : f.getGrantsBadgeToCompetences() ) {
                if ( !profile.hasBadge( competenceType ) )
                    profile.getCompetenceBadges().add( competenceType );

                var hasCompetence = !competenceService.findByProfileAndCompetenceType(
                    profile.getId(),
                    competenceType.getId()
                ).isEmpty();

                if ( !hasCompetence ) {
                    competenceService.create(
                        new CreateCompetenceDTO(
                        competenceType.getId().toString(),
                        "",
                        Competence.MIN_LEVEL
                        ),
                        profile
                    );
                }

                if ( !competenceTypeService.hasAccessToCompetenceType( competenceType, profile ) )
                    competenceTypeService.grantAccessToProfile( competenceType, profile );
            }
        }

        perfilRepository.saveAndFlush( profile );
    }

    public Collection<FolderFavorite> getFavorites( String idOrUsername ) {
        var profile = findByIdOrUsernameOrThrow( idOrUsername );
        return FolderService.getInstance().listFavorites( profile.getId() );
    }

    public Collection<Competence> getCompetences( String idOrUsername ) {
        var profileId = findByIdOrUsernameOrThrow( idOrUsername ).getId();

        return CompetenceService.getInstance().findByProfile( profileId )
            .stream()
            .sorted( Comparator.comparing( Competence::getCreationDate ).reversed() )
            .filter(Objects::nonNull)
            .toList();
    }

    public Collection<Education> getEducations( String idOrUsername ) {
        return findByIdOrUsernameOrThrow( idOrUsername )
            .getEducations()
            .stream()
            .sorted( Comparator.comparing( Education::getStartDate ).reversed() )
            .filter(Objects::nonNull)
            .toList();
    }

    public Collection<Experience> getExperiences( String idOrUsername ) {
        return findByIdOrUsernameOrThrow( idOrUsername )
            .getExperiences()
            .stream()
            .sorted( Comparator.comparing( Experience::getStartDate ).reversed() )
            .filter(Objects::nonNull)
            .toList();
    }

    public Collection<Group> getGroups( String idOrUsername ) {
        return findByIdOrUsernameOrThrow( idOrUsername )
            .getGroups()
            .stream()
            .sorted( Comparator.comparing( ProfileGroup::getJoined ).reversed() )
            .map( ProfileGroup::getGroup )
            .filter(Objects::nonNull)
            .toList();
    }

    public Collection<Link> getLinks( String idOrUsername ) {
        return findByIdOrUsernameOrThrow( idOrUsername )
            .getLinks();
    }

    public Optional<Profile> getProfileInSession() {
        var user = loginService.getUserInSession(false);
        if ( user == null || user.getProfile() == null || user.getProfile().getId() == null)
            return Optional.empty();
        return find( user.getProfile().getId() );
    }

    public @NotNull Profile getProfileInSessionOrThrow() {
        return getProfileInSession()
            .orElseThrow( this::makeProfileAccessErrorException );
    }

    private UniversiForbiddenAccessException makeProfileAccessErrorException() {

        // Force logout the user if the session is not valid
        loginService.logout();

        return new UniversiForbiddenAccessException( "Esta sessão não está logada em um perfil" );
    }

    public boolean isSessionOfProfile( Profile profile) {
        var loggedProfile = getProfileInSession();
        return loggedProfile.isPresent()
            && loggedProfile.get().getId().equals( profile.getId() );
    }

    private String validateFirstname( String firstname ) {
        firstname = firstname.trim();

        if ( firstname.isBlank() )
            throw new UniversiBadRequestException( "O nome não pode estar vazio" );

        if ( firstname.length() > MAX_NAME_LENGTH )
            throw new UniversiBadRequestException( "O nome não pode ter mais de " + MAX_NAME_LENGTH + " caracteres" );

        return firstname;
    }

    private String validateLastname( String lastname ) {
        lastname = lastname.trim();

        if ( lastname.length() > MAX_NAME_LENGTH )
            throw new UniversiBadRequestException( "O sobrenome não pode ter mais de " + MAX_NAME_LENGTH + " caracteres" );

        return lastname;
    }

    private @Nullable String validateBiography( String biography ) {
        biography = biography.trim();

        if ( biography.isBlank() )
            return null;

        if ( biography.length() > MAX_BIO_LENGTH )
            throw new UniversiBadRequestException( "A biografia não pode ter mais de " + MAX_BIO_LENGTH + " caracteres" );

        return biography;
    }

    @Override
    public boolean hasPermissionToEdit( Profile profile ) {
        return isSessionOfProfile( profile );
    }

    @Override
    public boolean hasPermissionToDelete(Profile profile) {
        return hasPermissionToEdit( profile ) || userService.isUserAdminSession();
    }

    @Override
    public boolean isValid( Profile profile ) {
        return profile != null && perfilRepository.existsByIdAndDeletedFalse( profile.getId() ) && UserService.getInstance().isValid( profile.getUser() );
    }
}
