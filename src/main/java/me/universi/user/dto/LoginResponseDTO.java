package me.universi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.user.entities.User;

public record LoginResponseDTO(
        @NotNull
        User user,
        @NotBlank
        String token
) {
}
