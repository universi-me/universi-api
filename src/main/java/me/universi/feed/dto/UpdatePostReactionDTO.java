package me.universi.feed.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePostReactionDTO(
        @NotNull
        String reaction
) {
}
