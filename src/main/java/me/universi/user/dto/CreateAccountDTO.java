package me.universi.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CreateAccountDTO(
        @Nullable
        String recaptchaToken,

        @NotNull
        String username,

        @NotNull
        String email,
        @NotNull
        String password,

        @NotNull
        String firstname,

        @NotNull
        String lastname
) {
}
