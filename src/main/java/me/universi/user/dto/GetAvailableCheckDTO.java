package me.universi.user.dto;

import jakarta.validation.constraints.NotNull;

public record GetAvailableCheckDTO(
    boolean available,

    @NotNull
    String reason
) {
}
