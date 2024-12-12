package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.group.DTO.CompetenceFilterDTO;
import me.universi.group.DTO.CompetenceInfoDTO;
import me.universi.group.DTO.ProfileWithCompetencesDTO;
import me.universi.group.DTO.UpdateGroupParticipantDTO;
import me.universi.group.services.GroupParticipantService;
import me.universi.profile.entities.Profile;
import me.universi.group.services.GroupService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/participants")
public class GroupParticipantController {
    private final GroupService groupService;
    private final GroupParticipantService groupParticipantService;

    public GroupParticipantController(GroupService groupService, GroupParticipantService groupParticipantService) {
        this.groupService = groupService;
        this.groupParticipantService = groupParticipantService;
    }

    @PatchMapping(value = "/join/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> participant_enter( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupParticipantService.joinGroup( id ) );
    }

    @PatchMapping(value = "/leave/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> participant_exit( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupParticipantService.leaveGroup( id ) );
    }

    @PatchMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> participant_add( @Valid @RequestBody UpdateGroupParticipantDTO updateGroupParticipantDTO ) {
        return ResponseEntity.ok( groupParticipantService.addParticipantGroup( updateGroupParticipantDTO ) );
    }

    @PatchMapping(value = "/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> participant_remove( @Valid @RequestBody UpdateGroupParticipantDTO updateGroupParticipantDTO ) {
        return ResponseEntity.ok( groupParticipantService.removeParticipantGroup( updateGroupParticipantDTO ) );
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> participant_list( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupParticipantService.listParticipantsByGroupId( id ) );
    }

    //Used when filtering participants based on their competences
    @PostMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProfileWithCompetencesDTO>> filterParticipants( @Valid @RequestBody CompetenceFilterDTO competenceFilter ){
        return ResponseEntity.ok( groupService.filterProfilesWithCompetences( competenceFilter ) );
    }

    @GetMapping(value = "/competences/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompetenceInfoDTO>> competences_list( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ){
        return ResponseEntity.ok( groupParticipantService.getGroupCompetencesByGroupId( id ) );
    }
}
