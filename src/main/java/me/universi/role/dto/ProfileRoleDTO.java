package me.universi.role.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.profile.entities.Profile;
import me.universi.role.entities.Role;

public record ProfileRoleDTO(
    @NotNull
    Profile profile,
    @NotNull
    Role role
) {}


