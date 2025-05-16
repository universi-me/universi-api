package me.universi.group.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.capacity.entidades.Folder;
import me.universi.group.DTO.CreateGroupDTO;
import me.universi.group.DTO.UpdateGroupDTO;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.image.controller.ImageMetadataController;
import me.universi.role.entities.Role;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> create(@Valid @RequestBody CreateGroupDTO createGroupDTO) {
        return new ResponseEntity<>(
            groupService.createGroup( createGroupDTO ),
            HttpStatus.CREATED
        );
    }

    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group>  update(@Valid @RequestBody UpdateGroupDTO updateGroupDTO) {
        return ResponseEntity.ok( groupService.updateGroup( updateGroupDTO ) );
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        groupService.deleteGroup( id );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> get( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.findOrThrow( id ) );
    }

    @GetMapping( path = "/from-path", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Group> getFromPath( @RequestParam( name = "group", required = true ) @Nullable String group ) {
        return ResponseEntity.ok( groupService.findByIdOrPathOrThrow( group ) );
    }

    @GetMapping(value = "/{id}/subgroups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Group>> list_subgroup( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.subGroups( id ) );
    }

    @GetMapping(value = "/parents", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Group>> list_available_parents() {
        return ResponseEntity.ok( groupService.findAll().stream().filter(Group::isCanCreateGroup).toList() );
    }

    @GetMapping(value = "/{id}/folders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Folder>> list_folders( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.listFolders( id ) );
    }

    @GetMapping(value = "/current-organization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> currentOrganization() {
        return ResponseEntity.ok( groupService.getOrganizationBasedInDomain() );
    }

    @GetMapping(value = "/{id}/image")
    public ResponseEntity<Resource> get_image( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ImageMetadataController.redirectToImage( groupService.findOrThrow( id ).getImage() );
    }

    @GetMapping(value = "/{id}/banner")
    public ResponseEntity<Resource> get_image_banner( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ImageMetadataController.redirectToImage( groupService.findOrThrow( id ).getBannerImage() );
    }

    @GetMapping(value = "/{id}/header")
    public ResponseEntity<Resource> get_image_header( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ImageMetadataController.redirectToImage( groupService.findOrThrow( id ).getHeaderImage() );
    }

    @GetMapping( path = "/{id}/roles", produces = "application/json" )
    public ResponseEntity<Collection<Role>> listRoles( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( groupService.findRoles( id ) );
    }
}
