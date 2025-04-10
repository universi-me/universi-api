package me.universi.experience.dto;

import java.util.Date;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CreateExperienceDTO(
    @NotNull
    String experienceType,

    @NotNull
    UUID institution,

    @NotNull
    String description,

    @NotNull
    Date startDate,

    @Nullable
    Date endDate
) {}
