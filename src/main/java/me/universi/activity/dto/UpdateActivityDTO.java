package me.universi.activity.dto;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

public record UpdateActivityDTO(
    Optional<String> name,
    Optional<String> description,
    Optional<String> type,
    Optional<List<@NotBlank String>> badges
) {}
