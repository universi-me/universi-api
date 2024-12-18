package me.universi.experience.controller;

import me.universi.experience.dto.CreateExperienceDTO;
import me.universi.experience.dto.UpdateExperienceDTO;
import me.universi.experience.entities.Experience;
import me.universi.experience.services.ExperienceService;

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
@RequestMapping( "/api/experiences" )
public class ExperienceController {

    private ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Experience> create( @Valid @RequestBody CreateExperienceDTO createExperienceDTO ) {
        return new ResponseEntity<>(
            experienceService.create( createExperienceDTO ),
            HttpStatus.CREATED
        );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Experience> update(
        @Valid @PathVariable @NotNull UUID id,
        @Valid @RequestBody UpdateExperienceDTO updateExperienceDTO
    ) {
        return ResponseEntity.ok( experienceService.update( id, updateExperienceDTO ) );
    }

    @DeleteMapping( path = "/{id}" )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull UUID id ) {
        experienceService.delete( id );
        return ResponseEntity.noContent().build();
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Experience> get( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( experienceService.findOrThrow( id ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Experience>> findAll() {
        return ResponseEntity.ok( experienceService.findAll() );
    }
}
