package me.universi.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import me.universi.profile.entities.Profile;
import me.universi.role.entities.Role;

@Schema( description = "Response body for listing Group participants with their Roles" )
public record ProfileRoleDTO(
    @NotNull
    Profile profile,
    @NotNull
    Role role
) {}


