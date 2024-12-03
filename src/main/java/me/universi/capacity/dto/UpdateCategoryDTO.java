package me.universi.capacity.dto;

import jakarta.annotation.Nullable;

public record UpdateCategoryDTO(
    @Nullable
    String name,

    @Nullable
    String image
) { }
