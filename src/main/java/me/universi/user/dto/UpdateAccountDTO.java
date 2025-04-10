package me.universi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountDTO(
        @NotBlank
        String password,
        @NotBlank
        String newPassword
) {
}
