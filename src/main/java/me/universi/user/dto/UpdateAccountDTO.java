package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateAccountDTO(
        @NotNull
        String password,
        @NotNull
        String newPassword
) {
}
