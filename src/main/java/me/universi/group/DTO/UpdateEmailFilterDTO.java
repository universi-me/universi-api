package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateEmailFilterDTO(
    @NotBlank
    UUID groupEmailFilterId,

    @NotBlank
    String email,

    @NotNull
    Boolean enabled,

    @NotBlank
    String type
) { }
