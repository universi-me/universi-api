package me.universi.group.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for removing a single Profile from a Group" )
public record RemoveGroupParticipantDTO(
        @NotBlank
        @JsonAlias( { "groupId", "groupPath" } )
        @Schema( description = "Group ID or path" )
        String group,

        @NotBlank
        @Schema( description = "Profile ID or username" )
        String participant
) { }
