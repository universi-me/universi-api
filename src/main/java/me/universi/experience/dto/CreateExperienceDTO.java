package me.universi.experience.dto;

import java.util.Date;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateExperienceDTO(
    @NotBlank
    String experienceType,

    @NotNull
    UUID institution,

    @NotBlank
    String description,

    @NotNull
    Date startDate,

    @Nullable
    Date endDate
) {}
