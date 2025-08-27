package me.universi.group.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;

import me.universi.activity.entities.Activity;
import me.universi.api.config.OpenAPIConfig;
import me.universi.capacity.entidades.Folder;
import me.universi.group.DTO.CreateGroupDTO;
import me.universi.group.DTO.UpdateGroupDTO;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.group.services.OrganizationService;
import me.universi.image.controller.ImageMetadataController;
import me.universi.profile.entities.Profile;
import me.universi.role.entities.Role;
import me.universi.util.SwaggerAnnotationUtils;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/groups")
@Tag(
    name = "Group",
    description = "Groups are a hierarchy-based entity that connects several other entities, most importantly Profiles"
)
public class GroupController {

    private final OrganizationService organizationService;
    private final GroupService groupService;

    public GroupController(GroupService groupService, OrganizationService organizationService) {
        this.groupService = groupService;
        this.organizationService = organizationService;
    }

    @Operation( summary = "Creates a new Group", description = "Users can only create subgroups of groups they have Permission from their Role in that parent Group" )
    @ApiResponse( responseCode = "201" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> create(@Valid @RequestBody CreateGroupDTO createGroupDTO) {
        return new ResponseEntity<>(
            groupService.createGroup( createGroupDTO ),
            HttpStatus.CREATED
        );
    }

    @Operation( summary = "Updates an existing Group", description = "Users can only update groups they are administrators or any group if they are system administrators" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group>  update(@Valid @RequestBody UpdateGroupDTO updateGroupDTO) {
        return ResponseEntity.ok( groupService.updateGroup( updateGroupDTO ) );
    }

    @Operation( summary = "Deletes an existing Group", description = "Users can only delete Groups they are administrators or any group if they are system administrators" )
    @ApiResponse( responseCode = "204" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        groupService.deleteGroup( id );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Fetches the Group with specified ID", description = "Users can only see Groups that are public or that they participate" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> get( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.findOrThrow( id ) );
    }

    @Operation( summary = "Fetches the Group with specified ID or path", description = "Users can only see Groups that are public or that they participate" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping( path = "/from-path", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Group> getFromPath( @RequestParam( name = "group", required = true ) @Nullable String group ) {
        return ResponseEntity.ok( groupService.findByIdOrPathOrThrow( group ) );
    }

    @Operation( summary = "Lists all subgroup of the Group with specified ID", description = "Users can only see Groups that are public or that they participate" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}/subgroups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Group>> list_subgroup( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.subGroups( id ) );
    }

    @Operation( summary = "Lists all Groups that can have subgroups", description = "Users can only see Groups that are public or that they participate", deprecated = true )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/parents", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Group>> list_available_parents() {
        return ResponseEntity.ok( groupService.findAll().stream().filter(Group::isCanCreateGroup).toList() );
    }

    @Operation( summary = "Lists all Folders in the Group with specified ID", description = "Users can only see them if they have Permission to do so" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}/folders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Folder>> list_folders( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.listFolders( id ) );
    }

    @Operation( summary = "Fetches the system organization" )
    @ApiResponse( responseCode = "200" )
    @GetMapping(value = "/current-organization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> currentOrganization() {
        return ResponseEntity.ok( organizationService.getOrganization() );
    }

    @Operation( summary = "Redirects to the specified Group's image" )
    @SwaggerAnnotationUtils.ApiResponses.ImageRedirect
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<Resource> get_image( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ImageMetadataController.redirectToImage( groupService.findOrThrow( id ).getImage() );
    }

    @Operation( summary = "Redirects to the specified Group's banner image" )
    @SwaggerAnnotationUtils.ApiResponses.ImageRedirect
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}/banner")
    public ResponseEntity<Resource> get_image_banner( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ImageMetadataController.redirectToImage( groupService.findOrThrow( id ).getBannerImage() );
    }

    @Operation( summary = "Redirects to the specified Group's header image" )
    @SwaggerAnnotationUtils.ApiResponses.ImageRedirect
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}/header")
    public ResponseEntity<Resource> get_image_header( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ImageMetadataController.redirectToImage( groupService.findOrThrow( id ).getHeaderImage() );
    }

    @Operation( summary = "Lists all Roles for the specified Group" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping( path = "/{id}/roles", produces = "application/json" )
    public ResponseEntity<Collection<Role>> listRoles( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( groupService.findRoles( id ) );
    }

    @Operation( summary = "Lists all administrators for the specified Group", description = "Only available to this Group administrators" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}/administrators", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> listAdmins( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.listAdministrators( id ) );
    }

    @Operation( summary = "Lists all Activities for the specified Group", description = "Only available if the user has READ Permission to this Group's subgroups" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/{id}/activities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Activity>> listActivities( @Valid @PathVariable UUID id ) {
        return ResponseEntity.ok( groupService.listActivities( id ) );
    }
}
