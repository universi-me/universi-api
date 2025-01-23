package me.universi.experience.dto;

import jakarta.annotation.Nullable;

public record UpdateExperienceTypeDTO(
    @Nullable
    String name
) { }
