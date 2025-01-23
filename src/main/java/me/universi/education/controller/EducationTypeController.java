package me.universi.education.controller;


import me.universi.education.dto.CreateEducationTypeDTO;
import me.universi.education.dto.UpdateEducationTypeDTO;
import me.universi.education.entities.EducationType;
import me.universi.education.services.EducationTypeService;

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

@RestController
@RequestMapping(value = "/education-types")
public class EducationTypeController {

    private EducationTypeService educationTypeService;

    public EducationTypeController(EducationTypeService educationTypeService){
        this.educationTypeService = educationTypeService;
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<EducationType> create( @Valid @RequestBody CreateEducationTypeDTO createEducationTypeDTO ) {
        return new ResponseEntity<>( educationTypeService.create( createEducationTypeDTO ), HttpStatus.CREATED );
    }

    @GetMapping( path = "/{idOrName}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<EducationType> get( @Valid @PathVariable @NotNull String idOrName ) {
        return ResponseEntity.ok( educationTypeService.findByIdOrNameOrThrow( idOrName ) );
    }

    @PatchMapping( path = "/{idOrName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<EducationType> update(
        @Valid @PathVariable @NotNull String idOrName,
        @Valid @RequestBody UpdateEducationTypeDTO updateEducationTypeDTO
    ) {
        return ResponseEntity.ok( educationTypeService.update( idOrName, updateEducationTypeDTO ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<EducationType>> findAll( ) {
        return ResponseEntity.ok( educationTypeService.findAll() );
    }

    @DeleteMapping( path = "/{idOrName}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull String idOrName ) {
        educationTypeService.delete( idOrName );
        return ResponseEntity.noContent().build();
    }
}
