package me.universi.group.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateGroupFeatureDTO(
        @NotNull
        UUID groupId,

        @NotNull( message = "Nome da feature não pode ser nulo" )
        String name,

        String description,
        Boolean enabled
) { }
