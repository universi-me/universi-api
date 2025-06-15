package me.universi.experience.controller;

import me.universi.experience.dto.CreateExperienceTypeDTO;
import me.universi.experience.dto.UpdateExperienceTypeDTO;
import me.universi.experience.entities.ExperienceType;
import me.universi.experience.services.ExperienceTypeService;

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
@RequestMapping( "/experience-types" )
public class ExperienceTypeController {

    private ExperienceTypeService experienceTypeService;

    public ExperienceTypeController(ExperienceTypeService typeExperienceService){
        this.experienceTypeService = typeExperienceService;
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ExperienceType> create( @Valid @RequestBody CreateExperienceTypeDTO createExperienceTypeDTO ) {
        return new ResponseEntity<>( experienceTypeService.create( createExperienceTypeDTO ), HttpStatus.CREATED );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<ExperienceType>> listAll() {
        return ResponseEntity.ok( experienceTypeService.findAll() );
    }

    @GetMapping( path = "/{idOrName}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ExperienceType> get( @Valid @PathVariable @NotNull String idOrName ) {
        return ResponseEntity.ok( experienceTypeService.findByIdOrNameOrThrow( idOrName ) );
    }

    @PatchMapping( path = "/{idOrName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ExperienceType> update(
        @Valid @PathVariable @NotNull String idOrName,
        @Valid @RequestBody UpdateExperienceTypeDTO updateExperienceTypeDTO
    ) {
        return ResponseEntity.ok( experienceTypeService.update( idOrName, updateExperienceTypeDTO ) );
    }

    @DeleteMapping( path = "/{idOrName}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull String idOrName ) {
        experienceTypeService.delete( idOrName );
        return ResponseEntity.noContent().build();
    }
}
