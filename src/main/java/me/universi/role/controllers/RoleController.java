package me.universi.role.controllers;

import java.util.UUID;

import me.universi.role.dto.CreateRoleDTO;
import me.universi.role.dto.UpdateRoleDTO;
import me.universi.role.entities.Role;
import me.universi.role.services.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping( "/roles" )
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping( path = "", consumes = "application/json", produces = "application/json" )
    public ResponseEntity<Role> create( @Valid @RequestBody CreateRoleDTO createRoleDTO ) {
        return new ResponseEntity<>( roleService.create( createRoleDTO ), HttpStatus.CREATED );
    }

    @PatchMapping( path = "/{id}", consumes = "application/json", produces = "application/json" )
    public ResponseEntity<Role> edit(
        @Valid @RequestBody UpdateRoleDTO updateRoleDTO,
        @Valid @PathVariable @NotNull UUID id
    ) {
        return ResponseEntity.ok( roleService.update( id, updateRoleDTO ) );
    }

    @DeleteMapping( path = "/{id}" )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull UUID id ) {
        roleService.delete( id );
        return ResponseEntity.noContent().build();
    }

    // assign roles
    @PatchMapping( value = "/{roleId}/assign/{profile}", produces = "application/json" )
    public ResponseEntity<Void> assign(
        @Valid @PathVariable @NotNull UUID roleId,
        @Valid @PathVariable @NotNull String profile
    ) {
        roleService.assignRole( roleId, profile );
        return ResponseEntity.noContent().build();
    }

    // assigned roles
    // todo: move to a more appropriate controller ( eg.: ProfileGroupController )
    @GetMapping( path = "/{groupId}/{profile}/role", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Role> assignedRole(
        @Valid @PathVariable @NotNull UUID groupId,
        @Valid @PathVariable @NotNull String profile
    ) {
        return ResponseEntity.ok( roleService.getAssignedRole( profile, groupId ) );
    }
}
