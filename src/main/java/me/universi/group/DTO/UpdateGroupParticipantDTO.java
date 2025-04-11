package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;

public record UpdateGroupParticipantDTO(
        @NotBlank
        String groupId,

        @NotBlank
        String participant
) { }
