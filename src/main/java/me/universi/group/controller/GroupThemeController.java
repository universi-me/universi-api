package me.universi.group.controller;

import jakarta.validation.Valid;
import me.universi.group.DTO.UpdateGroupThemeDTO;
import me.universi.group.entities.GroupSettings.GroupTheme;
import me.universi.group.services.GroupThemeService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/settings/themes")
public class GroupThemeController {
    private final GroupThemeService groupThemeService;

    public GroupThemeController(GroupThemeService groupThemeService) {
        this.groupThemeService = groupThemeService;
    }

    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupTheme> theme_update( @Valid @RequestBody UpdateGroupThemeDTO updateGroupThemeDTO ) {
        return ResponseEntity.ok(groupThemeService.updateTheme( updateGroupThemeDTO ));
    }
}
