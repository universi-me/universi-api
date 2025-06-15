package me.universi.group.controller;

import jakarta.validation.Valid;
import me.universi.group.DTO.UpdateGroupEnvironmentDTO;
import me.universi.group.entities.GroupEnvironment;
import me.universi.group.services.GroupEnvironmentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/settings/environments")
public class GroupEnvironmentController {
    private final GroupEnvironmentService groupEnvironmentService;

    public GroupEnvironmentController(GroupEnvironmentService groupEnvironmentService) {
        this.groupEnvironmentService = groupEnvironmentService;
    }

    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEnvironment> environment_update( @Valid @RequestBody UpdateGroupEnvironmentDTO updateGroupEnvironmentDTO ) {
        return ResponseEntity.ok(groupEnvironmentService.updateOrganizationEnvironment( updateGroupEnvironmentDTO ));
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEnvironment> environment_list() {
        return ResponseEntity.ok(groupEnvironmentService.getOrganizationEnvironment());
    }
}
