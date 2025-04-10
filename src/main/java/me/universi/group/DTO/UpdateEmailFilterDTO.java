package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateEmailFilterDTO(
    @NotNull
    UUID groupEmailFilterId,

    String email,
    Boolean enabled,
    String type
) { }
