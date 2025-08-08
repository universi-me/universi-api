package me.universi.group.DTO;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.group.entities.Group;
import me.universi.profile.entities.Profile;
import me.universi.role.entities.Role;

public record AddGroupParticipantDTO(
        @NotBlank
        @JsonAlias( { "groupId", "groupPath" } )
        String group,

        @NotBlank
        String participant,

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
