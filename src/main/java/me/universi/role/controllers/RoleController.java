package me.universi.role.controllers;

import java.util.Collection;
import java.util.UUID;

import me.universi.api.config.OpenAPIConfig;
import me.universi.role.dto.CreateRoleDTO;
import me.universi.role.dto.ProfileRoleDTO;
import me.universi.role.dto.UpdateRoleDTO;
import me.universi.role.entities.Role;
import me.universi.role.services.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping( "/roles" )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "Role",
    description = "Roles control user's Permissions inside of a specific Group and determine what they can access, create, change or delete"
)
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation( summary = "Creates a new Role in a Group", description = "Only Group administrators can create a Role of that Group" )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = "application/json", produces = "application/json" )
    public ResponseEntity<Role> create( @Valid @RequestBody CreateRoleDTO createRoleDTO ) {
        return new ResponseEntity<>( roleService.create( createRoleDTO ), HttpStatus.CREATED );
    }

    @Operation( summary = "Updates an existing Role of a Group", description = "Only Group administrators can update a Role of that Group" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = "application/json", produces = "application/json" )
    public ResponseEntity<Role> edit(
        @Valid @RequestBody UpdateRoleDTO updateRoleDTO,
        @Valid @PathVariable @NotNull UUID id
    ) {
        return ResponseEntity.ok( roleService.update( id, updateRoleDTO ) );
    }

    @Operation( summary = "Deletes an existing Role in a Group", description = "Only Group administrators can delete a Role in that Group. Members with that Role will be reassigned to the default member Role" )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{id}" )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull UUID id ) {
        roleService.delete( id );
        return ResponseEntity.noContent().build();
    }

    // assign roles
    @Operation( summary = "Assigns a Role to a Profile", description = "Only Group administrators can assign a Role in that Group" )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( value = "/{roleId}/assign/{profile}", produces = "application/json" )
    public ResponseEntity<Void> assign(
        @Valid @PathVariable @NotNull UUID roleId,
        @Valid @PathVariable @NotNull String profile
    ) {
        roleService.assignRole( roleId, profile );
        return ResponseEntity.noContent().build();
    }

    // get participants roles of a group
    @Operation( summary = "Lists all participants of a Group with their assigned Roles", description = "Only available to Group administrators" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{groupId}/participants", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<ProfileRoleDTO>> getParticipantsRoles(
        @Valid @PathVariable @NotNull UUID groupId
    ) {
        return ResponseEntity.ok( roleService.getParticipantsRoles( groupId ) );
    }

    // assigned roles
    // todo: move to a more appropriate controller ( eg.: ProfileGroupController )
    @Operation( summary = "Fetches the Role of the specified Profile in the specified Group", description = "Only available to Group administrators" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{groupId}/{profile}/role", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Role> assignedRole(
        @Valid @PathVariable @NotNull UUID groupId,
        @Valid @PathVariable @NotNull String profile
    ) {
        return ResponseEntity.ok( roleService.getAssignedRole( profile, groupId ) );
    }
}
