package me.universi.group.DTO;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.group.entities.Group;
import me.universi.profile.entities.Profile;
import me.universi.role.entities.Role;

@Schema( description = "Request body for adding a single Profile to a Group" )
public record AddGroupParticipantDTO(
        @NotBlank
        @JsonAlias( { "groupId", "groupPath" } )
        @Schema( description = "Group ID or path" )
        String group,

        @NotBlank
        @Schema( description = "Profile ID or username" )
        String participant,

        @Schema( description = "Role to be applied to the participant" )
        Optional<UUID> role
) {
    public AddGroupParticipantDTO( @NotNull Group group, @NotNull Profile participant, @Nullable Role role ) {
        this(
            group.getId().toString(),
            participant.getId().toString(),
            role == null
                ? Optional.empty()
                : Optional.of( role.getId() )
        );
    }
}
