package me.universi.profile.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.universi.api.config.OpenAPIConfig;
import me.universi.profile.dto.CreateDepartmentDTO;
import me.universi.profile.dto.UpdateDepartmentDTO;
import me.universi.profile.entities.Department;
import me.universi.profile.services.DepartmentService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping(
    path = "/departments",
    produces = MediaType.APPLICATION_JSON_VALUE
)
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "Department",
    description = "Departments are subdivisions of the company this organization represents and are used to group and filter its users"
)
public class DepartmentController {
    private final DepartmentService service;

    public DepartmentController(DepartmentService departmentService) {
        this.service = departmentService;
    }

    @Operation( summary = "Lists all Departments" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( "" )
    public ResponseEntity<List<Department>> list() {
        return ResponseEntity.ok( service.findAll() );
    }

    @Operation( summary = "Fetches a specified Department by ID or acronym" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( "/{id}" )
    public ResponseEntity<Department> get( @PathVariable String id ) {
        return ResponseEntity.ok( service.findByIdOrNameOrThrow( id ) );
    }

    @Operation( summary = "Creates a new Department", description = "Only system administrators can create new Departments" )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Department> create( @Valid @RequestBody CreateDepartmentDTO body ) {
        return new ResponseEntity<>( service.create( body ), HttpStatus.CREATED );
    }

    @Operation( summary = "Updates an existing Department", description = "Only system administrators can update Departments" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Department> update( @PathVariable String id, @Valid @RequestBody UpdateDepartmentDTO body ) {
        return ResponseEntity.ok( service.update( id, body ) );
    }

    @Operation( summary = "Deletes an existing Department", description = "Only system administrators can delete Departments" )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( "/{id}" )
    public ResponseEntity<Void> delete( @PathVariable String id ) {
        service.delete( id );
        return ResponseEntity.noContent().build();
    }
}
