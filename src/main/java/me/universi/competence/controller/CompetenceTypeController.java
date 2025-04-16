package me.universi.competence.controller;

import me.universi.competence.dto.CreateCompetenceTypeDTO;
import me.universi.competence.dto.MergeCompetenceTypeDTO;
import me.universi.competence.dto.UpdateCompetenceTypeDTO;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceTypeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RestController
@RequestMapping( "/competence-types" )
public class CompetenceTypeController {
    private final CompetenceTypeService competenceTypeService;

    public CompetenceTypeController(CompetenceTypeService competenceTypeService) {
        this.competenceTypeService = competenceTypeService;
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<CompetenceType> create( @Valid @RequestBody CreateCompetenceTypeDTO createCompetenceTypeDTO ) {
        return new ResponseEntity<>(
            competenceTypeService.create( createCompetenceTypeDTO ),
            HttpStatus.CREATED
        );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<CompetenceType> update(
        @Valid @PathVariable @NotNull String id,
        @Valid @RequestBody UpdateCompetenceTypeDTO updateCompetenceTypeDTO
    ) {
        return ResponseEntity.ok( competenceTypeService.update( id, updateCompetenceTypeDTO ) );
    }

    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull String id ) {
        competenceTypeService.delete( id );
        return ResponseEntity.noContent().build();
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<CompetenceType> get( @Valid @PathVariable @NotNull String id ) {
        return ResponseEntity.ok( competenceTypeService.findByIdOrNameOrThrow( id ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<CompetenceType>> findAll() {
        return ResponseEntity.ok( competenceTypeService.findAll() );
    }

    @PatchMapping( path = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> merge( @Valid @RequestBody MergeCompetenceTypeDTO mergeCompetenceTypeDTO ) {
        competenceTypeService.merge( mergeCompetenceTypeDTO );
        return ResponseEntity.noContent().build();
    }
}
