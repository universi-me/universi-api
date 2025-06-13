package me.universi.activity.controllers;

import me.universi.activity.dto.*;
import me.universi.activity.entities.Activity;
import me.universi.activity.services.*;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.*;


@RestController
@RequestMapping( ActivityController.PATH )
public class ActivityController {
    public static final String PATH = "/activities";

    private final ActivityService activityService;

    public ActivityController( ActivityService activityService ) {
        this.activityService = activityService;
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Activity> get( @PathVariable UUID id ) {
        return ResponseEntity.ok( activityService.findOrThrow( id ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Activity>> list( FilterActivityDTO dto ) {
        return ResponseEntity.ok( activityService.filter( dto ) );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Activity> create( @Valid @RequestBody CreateActivityDTO dto ) {
        return ResponseEntity
            .status( HttpStatus.CREATED )
            .body( activityService.create( dto ) );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Activity> update( @Valid @RequestBody UpdateActivityDTO dto, @PathVariable UUID id ) {
        return ResponseEntity.ok( activityService.update( id, dto ) );
    }

    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @PathVariable UUID id ) {
        activityService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
