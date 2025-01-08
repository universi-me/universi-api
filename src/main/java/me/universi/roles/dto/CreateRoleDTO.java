package me.universi.roles.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record CreateRoleDTO(
    @NotBlank
    String name,

    @Nullable
    String description,

    @NotBlank
    String group
) {}
