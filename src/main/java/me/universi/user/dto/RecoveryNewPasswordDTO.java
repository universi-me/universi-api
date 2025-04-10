package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;

public record RecoveryNewPasswordDTO(
        @NotNull
        String token,
        @NotNull
        String newPassword
) {
}
