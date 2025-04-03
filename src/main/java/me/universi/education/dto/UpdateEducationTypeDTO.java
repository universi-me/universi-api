package me.universi.education.dto;

import jakarta.annotation.Nullable;

public record UpdateEducationTypeDTO(
    @Nullable
    String name
) {}
