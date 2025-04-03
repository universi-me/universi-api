package me.universi.capacity.dto;

import java.util.UUID;

import jakarta.annotation.Nullable;

public record UpdateCategoryDTO(
    @Nullable
    String name,

    @Nullable
    UUID image
) { }
