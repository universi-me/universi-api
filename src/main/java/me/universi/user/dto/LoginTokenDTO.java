package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;

public record LoginTokenDTO(
        @NotNull
        String token
) {
}
