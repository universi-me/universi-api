package me.universi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginCodeDTO(
        @NotBlank
        String code
) {
}
