package me.universi.education.controller;


import me.universi.education.dto.CreateEducationDTO;
import me.universi.education.dto.UpdateEducationDTO;
import me.universi.education.entities.Education;
import me.universi.education.services.EducationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/api/educations" )
public class EducationController {

    private EducationService educationService;

    public EducationController(EducationService educationService){
        this.educationService = educationService;
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Education> update(
        @Valid @PathVariable @NotNull UUID id,
        @Valid @RequestBody UpdateEducationDTO updateEducationDTO
    ) {
        return ResponseEntity.ok( educationService.update( id, updateEducationDTO ) );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Education> create(
        @Valid @RequestBody CreateEducationDTO createEducationDTO
    ) {
        return new ResponseEntity<>( educationService.create( createEducationDTO) , HttpStatus.CREATED );
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Education> get( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( educationService.findOrThrow( id ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Education>> findAll() {
        return ResponseEntity.ok( educationService.findAll() );
    }

    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull UUID id ) {
        educationService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
