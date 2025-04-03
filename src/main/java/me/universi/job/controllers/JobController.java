package me.universi.job.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.universi.job.dto.CreateJobDTO;
import me.universi.job.dto.UpdateJobDTO;
import me.universi.job.entities.Job;
import me.universi.job.services.JobService;

@RestController
@RequestMapping( "/jobs" )
public class JobController {
    private final JobService jobService;

    public JobController (JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> get(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id
    ) {
        return ResponseEntity.ok( jobService.findOrThrow( id ) );
    }

    @GetMapping( value = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Job>> list(
        @RequestParam( name = "onlyOpen", defaultValue = "false" ) boolean onlyOpen,
        @RequestParam( name = "competenceTypesIds", required = false ) List<UUID> competenceTypesIds
    ) {
        return ResponseEntity.ok( jobService.findFiltered(onlyOpen, competenceTypesIds) );
    }

    @PostMapping( value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Job> create( @Valid @RequestBody CreateJobDTO createJobDTO ) {
        return new ResponseEntity<>( jobService.create( createJobDTO ), HttpStatus.CREATED );
    }

    @PatchMapping( value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Job> update(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id,
        @Valid @RequestBody UpdateJobDTO updateJobDTO
    ) {
        return ResponseEntity.ok( jobService.edit( id, updateJobDTO ) );
    }

    @PatchMapping( value = "/close/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Job> close(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id
    ) {
        return ResponseEntity.ok( jobService.close( id ) );
    }
}
