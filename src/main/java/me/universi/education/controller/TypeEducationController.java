package me.universi.education.controller;


import me.universi.education.dto.CreateTypeEducationDTO;
import me.universi.education.dto.UpdateTypeEducationDTO;
import me.universi.education.entities.TypeEducation;
import me.universi.education.servicies.TypeEducationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RestController
@RequestMapping(value = "/api/education-types")
public class TypeEducationController {

    private TypeEducationService typeEducationService;

    public TypeEducationController(TypeEducationService typeEducationService){
        this.typeEducationService = typeEducationService;
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<TypeEducation> create( @Valid @RequestBody CreateTypeEducationDTO createTypeEducationDTO ) {
        return new ResponseEntity<>( typeEducationService.create( createTypeEducationDTO ), HttpStatus.CREATED );
    }

    @GetMapping( path = "/{idOrName}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<TypeEducation> get( @Valid @PathVariable @NotNull String idOrName ) {
        return ResponseEntity.ok( typeEducationService.findByIdOrNameOrThrow( idOrName ) );
    }

    @PutMapping( path = "/{idOrName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<TypeEducation> update(
        @Valid @PathVariable @NotNull String idOrName,
        @Valid @RequestBody UpdateTypeEducationDTO updateTypeEducationDTO
    ) {
        return ResponseEntity.ok( typeEducationService.update( idOrName, updateTypeEducationDTO ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<TypeEducation>> findAll( ) {
        return ResponseEntity.ok( typeEducationService.findAll() );
    }

    @DeleteMapping( path = "/{idOrName}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull String idOrName ) {
        typeEducationService.delete( idOrName );
        return ResponseEntity.noContent().build();
    }
}
