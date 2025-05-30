package me.universi.activity.controllers;

import java.util.Collection;
import java.util.UUID;
import me.universi.activity.services.ActivityTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import me.universi.activity.dto.*;
import me.universi.activity.entities.ActivityType;

@RestController
@RequestMapping( "/activity-types" )
public class ActivityTypeController {
    private final ActivityTypeService activityTypeService;

    public ActivityTypeController(ActivityTypeService activityTypeService) {
        this.activityTypeService = activityTypeService;
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ActivityType> get( @PathVariable UUID id ) {
        return ResponseEntity.ok( activityTypeService.findOrThrow( id ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<ActivityType>> list() {
        return ResponseEntity.ok( activityTypeService.findAll() );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ActivityType> create( @Valid @RequestBody CreateActivityTypeDTO dto ) {
        return ResponseEntity
            .status( HttpStatus.CREATED )
            .body( activityTypeService.create( dto ) );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ActivityType> update( @Valid @RequestBody UpdateActivityTypeDTO dto, @PathVariable String id ) {
        return ResponseEntity.ok( activityTypeService.update( id, dto ) );
    }

    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @PathVariable String id ) {
        activityTypeService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
