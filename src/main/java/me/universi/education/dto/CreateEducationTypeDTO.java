package me.universi.education.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateEducationTypeDTO(
    @NotBlank
    String name
) {}
