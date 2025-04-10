package me.universi.competence.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCompetenceTypeDTO(
    @NotBlank
    String name
) {}
