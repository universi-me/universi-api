package me.universi.competence.controller;

import me.universi.api.config.OpenAPIConfig;
import me.universi.competence.dto.CreateCompetenceDTO;
import me.universi.competence.dto.UpdateCompetenceDTO;
import me.universi.competence.entities.Competence;
import me.universi.competence.services.CompetenceService;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "/competences" )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "Competence",
    description = "Competences are a Profile's proficiency in some subject"
)
public class CompetenceController {
    private final CompetenceService competenceService;

    public CompetenceController(CompetenceService competenceService) {
        this.competenceService = competenceService;
    }

    @Operation( summary = "Creates a new Competence for the current user" )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Competence> create( @Valid @RequestBody CreateCompetenceDTO createCompetenceDTO ) {
        return new ResponseEntity<>( competenceService.create( createCompetenceDTO ), HttpStatus.CREATED );
    }

    @Operation( summary = "Updates an existing Competence", description = "A Competence can only be edited by the user who created it or by a system administrator" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Competence> update(
        @Valid @PathVariable @NotNull UUID id,
        @Valid @RequestBody UpdateCompetenceDTO updateCompetenceDTO
    ) {
        return ResponseEntity.ok( competenceService.update( id, updateCompetenceDTO ) );
    }

    @Operation( summary = "Deletes an existing Competence", description = "A Competence can only be deleted by the user who created it or by a system administrator" )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull UUID id ) {
        competenceService.delete( id );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Fetches the specified Competence" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Competence> get( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( competenceService.findOrThrow( id ) );
    }

    @Operation( summary = "Lists all Competences" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Competence>> findAll() {
        return ResponseEntity.ok( competenceService.findAll() );
    }
}
