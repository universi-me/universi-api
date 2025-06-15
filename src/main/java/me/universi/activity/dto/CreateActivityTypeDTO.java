package me.universi.activity.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateActivityTypeDTO(
    @NotBlank String name
) { }
