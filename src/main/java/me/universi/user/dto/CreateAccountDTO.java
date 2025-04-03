package me.universi.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountDTO(
        @Nullable
        String recaptchaToken,

        @NotBlank
        String username,

        @NotBlank
        String email,
        @NotBlank
        String password,

        @NotBlank
        String firstname,

        @NotBlank
        String lastname
) {
}
