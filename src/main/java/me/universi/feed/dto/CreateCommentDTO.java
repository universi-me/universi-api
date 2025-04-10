package me.universi.feed.dto;

import jakarta.validation.constraints.NotNull;

public record CreateCommentDTO(
        @NotNull
        String content
) {
}
