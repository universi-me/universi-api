package me.universi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record GetAvailableCheckDTO(
    boolean available,

    @NotBlank
    String reason
) {
}
