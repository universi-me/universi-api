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

import me.universi.activity.entities.Activity;
import me.universi.activity.services.ActivityService;
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

@RestController
@RequestMapping( "/profiles" )
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

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Profile> profile() {
        return ResponseEntity.ok( profileService.getProfileInSessionOrThrow() );
    }

    @GetMapping( path = "/{idOrUsername}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Profile> get( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.findByIdOrUsernameOrThrow( idOrUsername ) );
    }

    @PatchMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Profile> update( @RequestBody UpdateProfileDTO updateProfileDTO ) {
        return ResponseEntity.ok( profileService.update( updateProfileDTO ) );
    }

    @GetMapping( path = "/{idOrUsername}/groups", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Group>> groups( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getGroups( idOrUsername ) );
    }

    @GetMapping( path = "/{idOrUsername}/links", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Link>> links( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getLinks( idOrUsername ) );
    }

    @GetMapping( path = "/{idOrUsername}/competences", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Competence>> competences( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getCompetences( idOrUsername ) );
    }

    @GetMapping( path = "/{idOrUsername}/educations", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Education>> educations( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getEducations( idOrUsername ) );
    }

    @GetMapping( path = "/{idOrUsername}/experiences", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Experience>> experiences( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getExperiences( idOrUsername ) );
    }

    @GetMapping( path = "/{idOrUsername}/favorites", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<FolderFavorite>> favorites( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( profileService.getFavorites( idOrUsername ) );
    }

    @GetMapping( path = "/{idOrUsername}/folders", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ProfileFoldersDTO> folders( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( new ProfileFoldersDTO(
            profileService.getFavorites( idOrUsername ),
            folderService.getAssignments( null, null, idOrUsername )
        ) );
    }

    @GetMapping( path = "/{idOrUsername}/activities", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Activity>> activities( @PathVariable String idOrUsername ) {
        return ResponseEntity.ok( activityService.findByProfile( idOrUsername ) );
    }

    @GetMapping( path = "/{idOrUsername}/image" )
    public ResponseEntity<Resource> image( @PathVariable String idOrUsername ) {
        return ImageMetadataController.redirectToImage( profileService.findByIdOrUsernameOrThrow( idOrUsername ).getImage() );
    }
}
