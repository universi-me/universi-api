package me.universi.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record RecoveryPasswordDTO(
        @Nullable
        String recaptchaToken,

        @NotNull
        String username
) {
}
