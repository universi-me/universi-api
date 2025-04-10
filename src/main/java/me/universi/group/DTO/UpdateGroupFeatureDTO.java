package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateGroupFeatureDTO(
        @NotNull
        @NotBlank
        UUID groupFeatureId,

        String description,
        Boolean enabled
) { }
