package me.universi.competence.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record MergeCompetenceTypeDTO(
    @NotNull
    UUID removedCompetenceType,

    @NotNull
    UUID remainingCompetenceType
) { }
