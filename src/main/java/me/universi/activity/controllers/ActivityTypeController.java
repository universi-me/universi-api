package me.universi.activity.controllers;

import java.util.Collection;
import java.util.UUID;
import me.universi.activity.services.ActivityTypeService;
import me.universi.api.config.OpenAPIConfig;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.universi.activity.dto.*;
import me.universi.activity.entities.ActivityType;

@RestController
@RequestMapping( "/activity-types" )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "ActivityType",
    description = "ActivityTypes different kinds of Activities, such as meetings and workshops"
)
public class ActivityTypeController {
    private final ActivityTypeService activityTypeService;

    public ActivityTypeController(ActivityTypeService activityTypeService) {
        this.activityTypeService = activityTypeService;
    }

    @Operation( summary = "Fetches the specified ActivityType" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ActivityType> get( @PathVariable UUID id ) {
        return ResponseEntity.ok( activityTypeService.findOrThrow( id ) );
    }

    @Operation( summary = "Lists all ActivityTypes" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<ActivityType>> list() {
        return ResponseEntity.ok( activityTypeService.findAll() );
    }

    @Operation( summary = "Creates a new ActivityType", description = "An ActivityType can only be created by a system administrator" )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ActivityType> create( @Valid @RequestBody CreateActivityTypeDTO dto ) {
        return ResponseEntity
            .status( HttpStatus.CREATED )
            .body( activityTypeService.create( dto ) );
    }

    @Operation( summary = "Updates an existing ActivityType", description = "An ActivityType can only be updated by a system administrator" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ActivityType> update( @Valid @RequestBody UpdateActivityTypeDTO dto, @PathVariable String id ) {
        return ResponseEntity.ok( activityTypeService.update( id, dto ) );
    }

    @Operation( summary = "Deletes an existing ActivityType", description = "An ActivityType can only be deleted by a system administrator" )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @PathVariable String id ) {
        activityTypeService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
