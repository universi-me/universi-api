package me.universi.institution.dto;

import jakarta.validation.constraints.NotNull;

public record CreateInstitutionDTO(
    @NotNull
    String name
) { }
