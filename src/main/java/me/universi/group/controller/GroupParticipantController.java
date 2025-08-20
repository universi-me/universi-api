package me.universi.group.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import me.universi.group.DTO.CompetenceFilterDTO;
import me.universi.group.DTO.CompetenceInfoDTO;
import me.universi.api.config.OpenAPIConfig;
import me.universi.group.DTO.AddGroupParticipantDTO;
import me.universi.group.DTO.ChangeGroupParticipantsDTO;
import me.universi.group.DTO.RemoveGroupParticipantDTO;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.services.GroupParticipantService;
import me.universi.profile.entities.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/group/participants")
@Tag(
    name = "GroupProfile",
    description = "GroupProfiles controls which Profiles are participants in which Groups, uniting these entities"
)
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
public class GroupParticipantController {
    private final GroupParticipantService groupParticipantService;

    public GroupParticipantController(GroupParticipantService groupParticipantService) {
        this.groupParticipantService = groupParticipantService;
    }

    @Operation( summary = "Joins specified Group", description = "You can only join a Group that can be freely joined" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping(value = "/join/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileGroup> join( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupParticipantService.join( id ) );
    }

    @Operation( summary = "Leaves specified Group", description = "You can only leave a Group you participate and cannot leave the organization" )
    @ApiResponse( responseCode = "204" )
    @PatchMapping(value = "/leave/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> leave( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        groupParticipantService.leave( id );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Updates participants of specified Group", description = "You can only change a Group's participants if you have the right Permission" )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( value = "/{id}/change", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> change( @PathVariable UUID id, @Valid @RequestBody ChangeGroupParticipantsDTO dto ) {
        groupParticipantService.changeParticipants( id, dto );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Adds a new participant to specified Group", deprecated = true )
    @ApiResponse( responseCode = "200" )
    @PatchMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileGroup> addParticipant( @Valid @RequestBody AddGroupParticipantDTO addGroupParticipantDTO ) {
        return ResponseEntity.ok( groupParticipantService.addParticipant( addGroupParticipantDTO ) );
    }

    @Operation( summary = "Removes a participant from specified Group", deprecated = true )
    @ApiResponse( responseCode = "204" )
    @PatchMapping(value = "/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeParticipant( @Valid @RequestBody RemoveGroupParticipantDTO removeGroupParticipantDTO ) {
        groupParticipantService.removeParticipant( removeGroupParticipantDTO );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Lists all participants of specified Group", deprecated = true )
    @ApiResponse( responseCode = "200" )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> listParticipants( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ) {
        return ResponseEntity.ok( groupParticipantService.listParticipantsByGroupId( id ) );
    }

    //Used when filtering participants based on their competences
    @Operation( summary = "Filters all participants of specified Group" )
    @ApiResponse( responseCode = "200" )
    @PostMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> filterParticipants( @Valid @RequestBody CompetenceFilterDTO competenceFilter ){
        return ResponseEntity.ok( groupParticipantService.filterParticipants( competenceFilter ) );
    }

    @Operation( summary = "Lists participants competences information of specified Group" )
    @ApiResponse( responseCode = "200" )
    @GetMapping(value = "/competences/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CompetenceInfoDTO>> listCompetences( @Valid @PathVariable @NotNull( message = "ID do grupo inválida" ) UUID id ){
        return ResponseEntity.ok( groupParticipantService.getGroupCompetencesByGroupId( id ) );
    }
}
