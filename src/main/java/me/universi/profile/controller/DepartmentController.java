package me.universi.profile.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
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
    path = "/department",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class DepartmentController {
    private final DepartmentService service;

    public DepartmentController(DepartmentService departmentService) {
        this.service = departmentService;
    }

    @GetMapping( "" )
    public ResponseEntity<List<Department>> list() {
        return ResponseEntity.ok( service.findAll() );
    }

    @GetMapping( "/{id}" )
    public ResponseEntity<Department> get( @PathVariable String id ) {
        return ResponseEntity.ok( service.findByIdOrNameOrThrow( id ) );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Department> create( @Valid @RequestBody CreateDepartmentDTO body ) {
        return new ResponseEntity<>( service.create( body ), HttpStatus.CREATED );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Department> update( @PathVariable String id, @Valid @RequestBody UpdateDepartmentDTO body ) {
        return ResponseEntity.ok( service.update( id, body ) );
    }

    @DeleteMapping( "/{id}" )
    public ResponseEntity<Void> delete( @PathVariable String id ) {
        service.delete( id );
        return ResponseEntity.noContent().build();
    }
}
