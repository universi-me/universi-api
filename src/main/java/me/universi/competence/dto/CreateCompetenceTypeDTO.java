package me.universi.competence.dto;

import jakarta.validation.constraints.NotNull;

public record CreateCompetenceTypeDTO(
    @NotNull
    String name
) {}
