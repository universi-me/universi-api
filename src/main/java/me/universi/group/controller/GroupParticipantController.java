package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.group.DTO.CompetenceFilterDTO;
import me.universi.group.DTO.CompetenceInfoDTO;
import me.universi.group.DTO.AddGroupParticipantDTO;
import me.universi.group.DTO.ChangeGroupParticipantsDTO;
import me.universi.group.DTO.RemoveGroupParticipantDTO;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.services.GroupParticipantService;
import me.universi.profile.entities.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group/participants")
public class GroupParticipantController {
    private final GroupParticipantService groupParticipantService;

    public GroupParticipantController(GroupParticipantService groupParticipantService) {
        this.groupParticipantService = groupParticipantService;
    }

    @PatchMapping(value = "/join/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileGroup> join( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupParticipantService.join( id ) );
    }

    @PatchMapping(value = "/leave/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> leave( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        groupParticipantService.leave( id );
        return ResponseEntity.noContent().build();
    }

    @PatchMapping( value = "/{id}/change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> change( @PathVariable UUID id, @Valid @RequestBody ChangeGroupParticipantsDTO dto ) {
        groupParticipantService.changeParticipants( id, dto );
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileGroup> addParticipant( @Valid @RequestBody AddGroupParticipantDTO addGroupParticipantDTO ) {
        return ResponseEntity.ok( groupParticipantService.addParticipant( addGroupParticipantDTO ) );
    }

    @PatchMapping(value = "/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeParticipant( @Valid @RequestBody RemoveGroupParticipantDTO removeGroupParticipantDTO ) {
        groupParticipantService.removeParticipant( removeGroupParticipantDTO );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> listParticipants( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupParticipantService.listParticipantsByGroupId( id ) );
    }

    //Used when filtering participants based on their competences
    @PostMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> filterParticipants( @Valid @RequestBody CompetenceFilterDTO competenceFilter ){
        return ResponseEntity.ok( groupParticipantService.filterParticipants( competenceFilter ) );
    }

    @GetMapping(value = "/competences/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompetenceInfoDTO>> listCompetences( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ){
        return ResponseEntity.ok( groupParticipantService.getGroupCompetencesByGroupId( id ) );
    }
}
