package me.universi.capacity.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import jakarta.annotation.Nullable;

public record CreateCategoryDTO(
    @NotNull
    String name,

    @Nullable
    UUID image
) { }
