package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEmailFilterDTO(
    @NotBlank
    String groupId,

    @NotBlank
    String email,

    @NotNull
    Boolean enabled,

    @NotBlank
    String type
) { }
