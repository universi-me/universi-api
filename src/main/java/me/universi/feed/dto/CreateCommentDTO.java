package me.universi.feed.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentDTO(
        @NotBlank
        String content
) {
}
