package me.universi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginTokenDTO(
        @NotBlank
        String token
) {
}
