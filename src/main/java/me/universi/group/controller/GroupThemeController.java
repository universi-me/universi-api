package me.universi.group.controller;

import jakarta.validation.Valid;
import me.universi.api.config.OpenAPIConfig;
import me.universi.group.DTO.UpdateGroupThemeDTO;
import me.universi.group.entities.GroupTheme;
import me.universi.group.services.GroupThemeService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/group/settings/themes")
@Tag(
    name = "GroupTheme",
    description = "GroupThemes stores CSS values to be used on the Web Client for an organization"
)
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
public class GroupThemeController {
    private final GroupThemeService groupThemeService;

    public GroupThemeController(GroupThemeService groupThemeService) {
        this.groupThemeService = groupThemeService;
    }

    @Operation( summary = "Updates the organization theme", description = "Only an organization administrator can change the GroupTheme" )
    @ApiResponse( responseCode = "201" )
    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupTheme> theme_update( @Valid @RequestBody UpdateGroupThemeDTO updateGroupThemeDTO ) {
        return ResponseEntity.ok(groupThemeService.updateTheme( updateGroupThemeDTO ));
    }
}
