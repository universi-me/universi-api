package me.universi.activity.dto;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

public record CreateActivityDTO(
    @NotBlank String name,
    @NotBlank String description,
    @NotBlank String type,
    Optional<List<@NotBlank String>> badges
) {}
