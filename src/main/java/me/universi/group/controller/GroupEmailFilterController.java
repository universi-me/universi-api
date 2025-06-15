package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.group.DTO.CreateEmailFilterDTO;
import me.universi.group.DTO.UpdateEmailFilterDTO;
import me.universi.group.entities.GroupEmailFilter;
import me.universi.group.services.GroupEmailFilterService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/settings/email-filters")
public class GroupEmailFilterController {

    private final GroupEmailFilterService groupEmailFilterService;

    public GroupEmailFilterController(GroupEmailFilterService groupEmailFilterService) {
        this.groupEmailFilterService = groupEmailFilterService;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEmailFilter> email_filter_create( @Valid @RequestBody CreateEmailFilterDTO createEmailFilterDTO ) {
        return new ResponseEntity<>(
            groupEmailFilterService.createEmailFilter( createEmailFilterDTO ),
            HttpStatus.CREATED
        );
    }

    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupEmailFilter> email_filter_update( @Valid @RequestBody UpdateEmailFilterDTO updateEmailFilterDTO ) {
        return ResponseEntity.ok( groupEmailFilterService.updateEmailFilter( updateEmailFilterDTO ) );
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> email_filter_delete( @Valid @PathVariable @NotNull( message = "ID do filtro de email inválido" ) UUID id ) {
        groupEmailFilterService.deleteEmailFilter( id );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupEmailFilter>> list( @PathVariable UUID groupId ) {
        return ResponseEntity.ok( groupEmailFilterService.listGroupEmailFilters( groupId ) );
    }
}
