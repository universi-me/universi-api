package me.universi.experience.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateExperienceTypeDTO(
    @NotBlank
    String name
) {}
