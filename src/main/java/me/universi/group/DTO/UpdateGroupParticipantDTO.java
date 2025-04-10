package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateGroupParticipantDTO(
        @NotNull
        UUID groupId,

        @NotNull
        String participant
) { }
