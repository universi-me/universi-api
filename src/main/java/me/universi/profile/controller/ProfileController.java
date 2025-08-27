package me.universi.profile.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.universi.activity.entities.Activity;
import me.universi.activity.services.ActivityService;
import me.universi.api.config.OpenAPIConfig;
import me.universi.capacity.entidades.FolderFavorite;
import me.universi.capacity.service.FolderService;
import me.universi.competence.entities.Competence;
import me.universi.education.entities.Education;
import me.universi.experience.entities.Experience;
import me.universi.group.entities.Group;
import me.universi.image.controller.ImageMetadataController;
import me.universi.link.entities.Link;
import me.universi.profile.dto.ProfileFoldersDTO;
import me.universi.profile.dto.UpdateProfileDTO;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;
import me.universi.util.SwaggerAnnotationUtils;

@RestController
@RequestMapping( "/profiles" )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "Profile",
    description = "Profiles store most of the platform's users data, such as name and biography"
)
public class ProfileController {
    private final ProfileService profileService;
    private final FolderService folderService;
    private final ActivityService activityService;

    @Value( "${server.servlet.context-path}" )
    private String contextPath;

    public ProfileController(ProfileService profileService, FolderService folderService, ActivityService activityService) {
        this.profileService = profileService;
        this.folderService = folderService;
        this.activityService = activityService;
    }

    @Operation( summary = "Fetches your own Profile" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Profile> profile() {
        return ResponseEntity.ok( profileService.getProfileInSessionOrThrow() );
    }

    @Operation( summary = "Fetches the specified Profile by ID or username" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Profile> get( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.findByIdOrUsernameOrThrow( idOrUsername ) );
    }

    @Operation( summary = "Updates your own Profile" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Profile> update( @RequestBody UpdateProfileDTO updateProfileDTO ) {
        return ResponseEntity.ok( profileService.update( updateProfileDTO ) );
    }

    @Operation( summary = "Lists all Groups the specified Profile participates" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/groups", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Group>> groups( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getGroups( idOrUsername ) );
    }

    @Operation( summary = "Lists all Links associated with specified Profile" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/links", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Link>> links( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getLinks( idOrUsername ) );
    }

    @Operation( summary = "Lists all Competences associated with specified Profile" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/competences", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Competence>> competences( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getCompetences( idOrUsername ) );
    }

    @Operation( summary = "Lists all Educations associated with specified Profile" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/educations", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Education>> educations( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getEducations( idOrUsername ) );
    }

    @Operation( summary = "Lists all Experiences associated with specified Profile" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/experiences", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Experience>> experiences( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getExperiences( idOrUsername ) );
    }

    @Operation( summary = "Lists all favorite Folders by specified Profile" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/favorites", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<FolderFavorite>> favorites( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getFavorites( idOrUsername ) );
    }

    @Operation( summary = "Lists all Folders assigned to specified Profile" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/folders", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ProfileFoldersDTO> folders( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( new ProfileFoldersDTO(
            profileService.getFavorites( idOrUsername ),
            folderService.getAssignments( null, null, idOrUsername )
        ) );
    }

    @Operation( summary = "Lists all Activities the specified Profile participates" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrUsername}/activities", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Activity>> activities( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( activityService.findByProfile( idOrUsername ) );
    }

    @Operation( summary = "Redirects to the specified Profile's image" )
    @SwaggerAnnotationUtils.ApiResponses.ImageRedirect
    @GetMapping( path = "/{idOrUsername}/image" )
    public ResponseEntity<Resource> image( @PathVariable String idOrUsername ) {
        return ImageMetadataController.redirectToImage( profileService.findByIdOrUsernameOrThrow( idOrUsername ).getImage() );
    }
}
