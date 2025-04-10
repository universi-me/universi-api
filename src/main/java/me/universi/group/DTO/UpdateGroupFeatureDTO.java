package me.universi.group.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateGroupFeatureDTO(
        @NotNull
        UUID groupFeatureId,

        String description,
        Boolean enabled
) { }
