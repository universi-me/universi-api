package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;

public record LoginCodeDTO(
        @NotNull
        String code
) {
}
