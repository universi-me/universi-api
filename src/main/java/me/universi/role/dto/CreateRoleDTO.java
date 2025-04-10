package me.universi.role.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CreateRoleDTO(
    @NotNull
    String name,

    @Nullable
    String description,

    @NotNull
    String group
) {}
