package me.universi.education.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTypeEducationDTO(
    @NotBlank
    String name
) {}
