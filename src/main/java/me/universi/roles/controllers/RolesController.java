package me.universi.roles.controllers;

import java.util.UUID;

import me.universi.roles.dto.CreateRoleDTO;
import me.universi.roles.dto.UpdateRoleDTO;
import me.universi.roles.entities.Roles;
import me.universi.roles.services.RolesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping( "/api/roles" )
public class RolesController {
    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping( path = "/", consumes = "application/json", produces = "application/json" )
    public ResponseEntity<Roles> create( @Valid @RequestBody CreateRoleDTO createRoleDTO ) {
        return new ResponseEntity<>( rolesService.create( createRoleDTO ), HttpStatus.CREATED );
    }

    @PatchMapping( path = "/{id}", consumes = "application/json", produces = "application/json" )
    public ResponseEntity<Roles> edit(
        @Valid @RequestBody UpdateRoleDTO updateRoleDTO,
        @Valid @PathVariable @NotNull UUID id
    ) {
        return ResponseEntity.ok( rolesService.update( id, updateRoleDTO ) );
    }

    @DeleteMapping( path = "/{id}" )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull UUID id ) {
        rolesService.delete( id );
        return ResponseEntity.noContent().build();
    }

    // assign roles
    @PatchMapping( value = "/{roleId}/assign/{profile}", produces = "application/json" )
    public ResponseEntity<Void> assign(
        @Valid @PathVariable @NotNull UUID roleId,
        @Valid @PathVariable @NotNull String profile
    ) {
        rolesService.assignRole( roleId, profile );
        return ResponseEntity.noContent().build();
    }

    // assigned roles
    // todo: move to a more appropriate controller ( eg.: ProfileGroupController )
    @GetMapping( path = "/{groupId}/{profile}/role", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Roles> assignedRole(
        @Valid @PathVariable @NotNull UUID groupId,
        @Valid @PathVariable @NotNull String profile
    ) {
        return ResponseEntity.ok( rolesService.getAssignedRoles( profile, groupId ) );
    }
}
