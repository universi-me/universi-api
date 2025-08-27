package me.universi.competence.controller;

import me.universi.api.config.OpenAPIConfig;
import me.universi.competence.dto.CreateCompetenceTypeDTO;
import me.universi.competence.dto.MergeCompetenceTypeDTO;
import me.universi.competence.dto.UpdateCompetenceTypeDTO;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.services.CompetenceTypeService;

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

import java.util.List;

@RestController
@RequestMapping( "/competence-types" )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "CompetenceType",
    description = "CompetenceTypes are subjects a Profile can create a Competence on"
)
public class CompetenceTypeController {
    private final CompetenceTypeService competenceTypeService;

    public CompetenceTypeController(CompetenceTypeService competenceTypeService) {
        this.competenceTypeService = competenceTypeService;
    }

    @Operation( summary = "Creates a new unreviewed CompetenceType" )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<CompetenceType> create( @Valid @RequestBody CreateCompetenceTypeDTO createCompetenceTypeDTO ) {
        return new ResponseEntity<>(
            competenceTypeService.create( createCompetenceTypeDTO ),
            HttpStatus.CREATED
        );
    }

    @Operation( summary = "Updates an existing CompetenceType", description = "Only a system administrator can update a CompetenceType." )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<CompetenceType> update(
        @Valid @PathVariable @NotNull String id,
        @Valid @RequestBody UpdateCompetenceTypeDTO updateCompetenceTypeDTO
    ) {
        return ResponseEntity.ok( competenceTypeService.update( id, updateCompetenceTypeDTO ) );
    }

    @Operation( summary = "Deletes an existing CompetenceType", description = "Only a system administrator can delete a CompetenceType." )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull String id ) {
        competenceTypeService.delete( id );
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Fetches a specific CompetenceType by it's ID or name",
        description = "Regular users can only see CompetenceTypes that were reviewed or that they created while system administrators have no restrictions."
    )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<CompetenceType> get( @Valid @PathVariable @NotNull String id ) {
        return ResponseEntity.ok( competenceTypeService.findByIdOrNameOrThrow( id ) );
    }

    @Operation(
        summary = "Lists all CompetenceTypes",
        description = "Regular users can only see CompetenceTypes that were reviewed or that they created while system administrators have no restrictions."
    )
    @ApiResponse( responseCode = "201" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<CompetenceType>> findAll() {
        return ResponseEntity.ok( competenceTypeService.findAll() );
    }

    @Operation(
        summary = "Merges an unreviewed CompetenceType into a reviewed one",
        description = "Deletes a CompetenceType and changes every use of it to a different CompetenceType. Only an system administrator can perform this operation."
    )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( path = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> merge( @Valid @RequestBody MergeCompetenceTypeDTO mergeCompetenceTypeDTO ) {
        competenceTypeService.merge( mergeCompetenceTypeDTO );
        return ResponseEntity.noContent().build();
    }
}
