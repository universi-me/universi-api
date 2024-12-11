package me.universi.institution.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.universi.institution.dto.CreateInstitutionDTO;
import me.universi.institution.dto.UpdateInstitutionDTO;
import me.universi.institution.entities.Institution;
import me.universi.institution.services.InstitutionService;

@RestController
@RequestMapping( "/api/institutions" )
public class InstitutionController {
    private InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Institution> get( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( institutionService.findOrThrow( id ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Institution>> listAll() {
        return ResponseEntity.ok( institutionService.findAll() );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Institution> create( @Valid @RequestBody CreateInstitutionDTO createInstitutionDTO ) {
        return new ResponseEntity<>( institutionService.create( createInstitutionDTO ), HttpStatus.CREATED );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Institution> edit(
        @Valid @PathVariable @NotNull UUID id,
        @Valid @RequestBody UpdateInstitutionDTO updateInstitutionDTO
    ) {
        return ResponseEntity.ok( institutionService.edit( id, updateInstitutionDTO ) );
    }

    @DeleteMapping( path = "/{id}" )
    public ResponseEntity<Void> remove( @Valid @PathVariable @NotNull UUID id ) {
        institutionService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
