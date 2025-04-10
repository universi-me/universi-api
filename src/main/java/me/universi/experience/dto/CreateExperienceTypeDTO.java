package me.universi.experience.dto;

import jakarta.validation.constraints.NotNull;

public record CreateExperienceTypeDTO(
    @NotNull
    String name
) {}
