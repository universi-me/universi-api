package me.universi.group.controller;

import java.util.Collection;
import java.util.UUID;

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
import me.universi.api.config.OpenAPIConfig;
import me.universi.group.DTO.CreateGroupTypeDTO;
import me.universi.group.DTO.UpdateGroupTypeDTO;
import me.universi.group.entities.GroupType;
import me.universi.group.services.GroupTypeService;

@RestController
@RequestMapping( "/group-types" )
@Tag(
    name = "GroupType",
    description = "GroupTypes are used to differentiate kinds of Groups, such as Departments or Teams"
)
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
public class GroupTypeController {
    private final GroupTypeService groupTypeService;

    public GroupTypeController( GroupTypeService groupTypeService ) {
        this.groupTypeService = groupTypeService;
    }

    @Operation( summary = "Fetches specified GroupType" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<GroupType> get( @PathVariable UUID id ) {
        return ResponseEntity.ok( groupTypeService.findOrThrow( id ) );
    }

    @Operation( summary = "Lists all GroupTypes available" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<GroupType>> list() {
        return ResponseEntity.ok( groupTypeService.findAll() );
    }

    @Operation( summary = "Creates a new GroupType", description = "Only a system administrator may create new GroupTypes" )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<GroupType> create( @Valid @RequestBody CreateGroupTypeDTO dto ) {
        return ResponseEntity
            .status( HttpStatus.CREATED )
            .body( groupTypeService.create( dto ) );
    }

    @Operation( summary = "Updates an existing GroupType", description = "Only a system administrator may update an existing GroupType" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<GroupType> update( @Valid @RequestBody UpdateGroupTypeDTO dto, @PathVariable String id ) {
        return ResponseEntity.ok( groupTypeService.update( id, dto ) );
    }

    @Operation( summary = "Deletes the specified ActivityType", description = "Only a system administrator may delete a GroupType" )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @PathVariable String id ) {
        groupTypeService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
