package me.universi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record RecoveryNewPasswordDTO(
        @NotBlank
        String token,
        @NotBlank
        String newPassword
) {
}
