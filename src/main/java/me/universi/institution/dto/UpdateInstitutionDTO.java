package me.universi.institution.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateInstitutionDTO(
    @NotNull
    String name
) { }
