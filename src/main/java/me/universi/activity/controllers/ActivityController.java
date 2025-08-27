package me.universi.activity.controllers;

import me.universi.activity.dto.*;
import me.universi.activity.entities.Activity;
import me.universi.activity.services.*;
import me.universi.api.config.OpenAPIConfig;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.*;


@RestController
@RequestMapping( ActivityController.PATH )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "Activity",
    description = "Activities store events external to the Universi.me system, like meetings and workshops on the real world"
)
public class ActivityController {
    public static final String PATH = "/activities";

    private final ActivityService activityService;

    public ActivityController( ActivityService activityService ) {
        this.activityService = activityService;
    }

    @Operation( summary = "Fetches the specified Activity" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Activity> get( @PathVariable UUID id ) {
        return ResponseEntity.ok( activityService.findOrThrow( id ) );
    }

    @Operation( summary = "Filters all Activities" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<Activity>> list( FilterActivityDTO dto ) {
        return ResponseEntity.ok( activityService.filter( dto ) );
    }

    @Operation( summary = "Creates a new Activity", description = "An Activity requires a subgroup of the specified parent Group, so the user needs Permission to create subgroups on that Group" )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Activity> create( @Valid @RequestBody CreateActivityDTO dto ) {
        return ResponseEntity
            .status( HttpStatus.CREATED )
            .body( activityService.create( dto ) );
    }

    @Operation( summary = "Updates an existing Activity", description = "An Activity can only be updated by it's Activity Group administrators" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Activity> update( @Valid @RequestBody UpdateActivityDTO dto, @PathVariable UUID id ) {
        return ResponseEntity.ok( activityService.update( id, dto ) );
    }

    @Operation( summary = "Deletes an existing Activity", description = "An Activity can only be deleted by it's Activity Group administrators" )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @PathVariable UUID id ) {
        activityService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
