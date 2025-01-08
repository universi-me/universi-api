package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.capacity.entidades.Folder;
import me.universi.group.DTO.CreateGroupDTO;
import me.universi.group.DTO.UpdateGroupDTO;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.roles.entities.Roles;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
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

    @DeleteMapping(value = "/{id}/subgroups/{subgroupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> remove(@Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id,
                                       @Valid @PathVariable @NotNull( message = "ID do subgrupo inválido" ) UUID subgroupId ) {
        groupService.removeSubgroup( id, subgroupId );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        groupService.deleteGroup( id );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> get( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupService.getGroupByGroupIdOrGroupPath(id, null) );
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
    public ResponseEntity<Void> get_image( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return groupService.getGroupImage( id );
    }

    @GetMapping(value = "/{id}/banner")
    public ResponseEntity<Void> get_image_banner( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return groupService.getBannerImage( id );
    }

    @GetMapping(value = "/{id}/header")
    public ResponseEntity<Void> get_image_header( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return groupService.getHeaderImage( id );
    }

    @GetMapping( path = "/{id}/roles", produces = "application/json" )
    public ResponseEntity<Collection<Roles>> listRoles( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( groupService.findRoles( id ) );
    }
}
