package me.universi.group.DTO;

import jakarta.validation.constraints.NotNull;

public record CreateEmailFilterDTO(
    @NotNull
    String groupId,

    @NotNull
    String email,

    @NotNull
    Boolean enabled,

    @NotNull
    String type
) { }
