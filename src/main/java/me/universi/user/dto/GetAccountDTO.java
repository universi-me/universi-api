package me.universi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;

import io.swagger.v3.oas.annotations.media.Schema;
import me.universi.role.entities.Role;
import me.universi.user.entities.User;

@Schema( description = "User data alongside a list of Roles for each Group the User is a participant" )
public record GetAccountDTO(
        @NotBlank
        User user,

        @NotNull
        Collection<Role> roles
) { }
