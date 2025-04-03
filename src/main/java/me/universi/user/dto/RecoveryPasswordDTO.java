package me.universi.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record RecoveryPasswordDTO(
        @Nullable
        String recaptchaToken,

        @NotBlank
        String username
) {
}
