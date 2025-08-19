package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;

import me.universi.api.config.OpenAPIConfig;
import me.universi.group.DTO.CreateEmailFilterDTO;
import me.universi.group.DTO.UpdateEmailFilterDTO;
import me.universi.group.entities.GroupEmailFilter;
import me.universi.group.services.GroupEmailFilterService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/group/settings/email-filters")
@Tag(
    name = "GroupEmailFilter",
    description = "GroupEmailFilters are filters applied to user registering in the platform. A new user's email must pass at least one GroupEmailFilter"
)
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
public class GroupEmailFilterController {

    private final GroupEmailFilterService groupEmailFilterService;

    public GroupEmailFilterController(GroupEmailFilterService groupEmailFilterService) {
        this.groupEmailFilterService = groupEmailFilterService;
    }

    @Operation( summary = "Creates a new GroupEmailFilter", description = "Only system administrators can create GroupEmailFilters" )
    @ApiResponse( responseCode = "201" )
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEmailFilter> email_filter_create( @Valid @RequestBody CreateEmailFilterDTO createEmailFilterDTO ) {
        return new ResponseEntity<>(
            groupEmailFilterService.createEmailFilter( createEmailFilterDTO ),
            HttpStatus.CREATED
        );
    }

    @Operation( summary = "Updates an existing GroupEmailFilter", description = "Only system administrators can update GroupEmailFilters" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEmailFilter> email_filter_update( @Valid @RequestBody UpdateEmailFilterDTO updateEmailFilterDTO ) {
        return ResponseEntity.ok( groupEmailFilterService.updateEmailFilter( updateEmailFilterDTO ) );
    }

    @Operation( summary = "Deletes an existing GroupEmailFilter", description = "Only system administrators can delete GroupEmailFilters" )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> email_filter_delete( @Valid @PathVariable @NotNull( message = "ID do filtro de email inválido" ) UUID id ) {
        groupEmailFilterService.deleteEmailFilter( id );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Lists all GroupEmailFilter of the specified Group", description = "Only system administrators can list the organization GroupEmailFilters" )
    @ApiResponse( responseCode = "200" )
    @GetMapping(value = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupEmailFilter>> list( @PathVariable UUID groupId ) {
        return ResponseEntity.ok( groupEmailFilterService.listGroupEmailFilters( groupId ) );
    }
}
