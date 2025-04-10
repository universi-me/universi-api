package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import me.universi.role.entities.Role;
import me.universi.user.entities.User;

public record GetAccountDTO(
        @NotNull
        User user,

        @NotNull
        Collection<Role> roles
) { }
