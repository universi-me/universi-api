package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateGroupFeatureDTO(
        @NotNull
        @NotBlank
        UUID groupFeatureId,

        @NotNull( message = "Nome da feature não pode ser nulo" )
        @NotBlank
        String name,

        String description,
        Boolean enabled
) { }
