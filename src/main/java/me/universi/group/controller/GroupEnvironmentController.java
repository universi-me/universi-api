package me.universi.group.controller;

import jakarta.validation.Valid;
import me.universi.api.config.OpenAPIConfig;
import me.universi.group.DTO.UpdateGroupEnvironmentDTO;
import me.universi.group.entities.GroupEnvironment;
import me.universi.group.services.GroupEnvironmentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/group/settings/environments")
@Tag(
    name = "GroupEnvironment",
    description = "GroupEnvironment are settings an organization runs on, for example, if Google Login or Keycloak Login is enabled"
)
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
public class GroupEnvironmentController {
    private final GroupEnvironmentService groupEnvironmentService;

    public GroupEnvironmentController(GroupEnvironmentService groupEnvironmentService) {
        this.groupEnvironmentService = groupEnvironmentService;
    }

    @Operation( summary = "Updates this organization GroupEnvironment", description = "Only organization administrators can update the GroupEnvironment" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEnvironment> environment_update( @Valid @RequestBody UpdateGroupEnvironmentDTO updateGroupEnvironmentDTO ) {
        return ResponseEntity.ok(groupEnvironmentService.updateOrganizationEnvironment( updateGroupEnvironmentDTO ));
    }

    @Operation( summary = "Fetches this organization GroupEnvironment", description = "Only organization administrators can fetch the GroupEnvironment" )
    @ApiResponse( responseCode = "200" )
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEnvironment> environment_list() {
        return ResponseEntity.ok(groupEnvironmentService.getOrganizationEnvironment());
    }
}
