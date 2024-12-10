package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.group.services.GroupAdminService;
import me.universi.profile.entities.Profile;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/settings/admin")
public class GroupAdminController {
    private final GroupAdminService groupAdminService;

    public GroupAdminController(GroupAdminService groupAdminService) {
        this.groupAdminService = groupAdminService;
    }

    // list administrators of group
    @GetMapping(value = "/{id}/administrators", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> admin_list( @Valid @PathVariable @NotNull( message = "ID do grupo inválido" ) UUID id ) {
        return ResponseEntity.ok( groupAdminService.listAdmininistratorsByGroupId( id ) );
    }
}
