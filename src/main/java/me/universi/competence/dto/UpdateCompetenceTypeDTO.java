package me.universi.competence.dto;

import jakarta.annotation.Nullable;

public record UpdateCompetenceTypeDTO(
    @Nullable
    String name,

    @Nullable
    Boolean reviewed
) {}
