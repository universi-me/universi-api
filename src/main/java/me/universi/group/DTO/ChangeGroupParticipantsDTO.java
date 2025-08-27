package me.universi.group.DTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for changing a Group's participants" )
public record ChangeGroupParticipantsDTO(
    @Schema( description = "A list of users to add to the Group, optionally with their Roles" )
    Optional<List<AddParticipantRecord>> add,
    @Schema( description = "A list of Profile IDs or usernames to remove from the Group" )
    Optional<List<String>> remove
) {
    public record AddParticipantRecord(
        @JsonAlias( { "profileId", "username" } )
        @Schema( description = "Profile ID or username" )
        @NotBlank String profile,
        @Schema( description = "Role ID to be applied to this participant" )
        Optional<UUID> role
    ) {}
}
