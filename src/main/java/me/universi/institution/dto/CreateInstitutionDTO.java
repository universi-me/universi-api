package me.universi.institution.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateInstitutionDTO(
    @NotBlank
    String name
) { }
