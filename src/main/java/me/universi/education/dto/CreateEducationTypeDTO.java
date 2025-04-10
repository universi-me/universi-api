package me.universi.education.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEducationTypeDTO(
    @NotNull
    String name
) {}
