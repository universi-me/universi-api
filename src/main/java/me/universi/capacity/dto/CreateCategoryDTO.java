package me.universi.capacity.dto;

import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record CreateCategoryDTO(
    @NotBlank
    String name,

    @Nullable
    UUID image
) { }
