package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.group.DTO.CreateGroupFeatureDTO;
import me.universi.group.DTO.UpdateGroupFeatureDTO;
import me.universi.group.entities.GroupSettings.GroupFeatures;
import me.universi.group.services.GroupFeatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/settings/features")
public class GroupFeaturesController {
    private final GroupFeatureService groupFeatureService;

    public GroupFeaturesController(GroupFeatureService groupFeatureService) {
        this.groupFeatureService = groupFeatureService;
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupFeatures> features_create( @Valid @RequestBody CreateGroupFeatureDTO createGroupFeatureDTO ) {
        return new ResponseEntity<>(
            groupFeatureService.createFeature( createGroupFeatureDTO ),
            HttpStatus.CREATED
        );
    }

    @PatchMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupFeatures> features_update( @Valid @RequestBody UpdateGroupFeatureDTO updateGroupFeatureDTO ) {
        return ResponseEntity.ok( groupFeatureService.updateFeature( updateGroupFeatureDTO ) );
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> features_delete( @Valid @PathVariable @NotNull( message = "ID da feature inválida" ) UUID id ) {
        groupFeatureService.deleteFeature( id );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupFeatures>> features_list( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupFeatureService.listFeaturesByGroupId( id ) );
    }
}
