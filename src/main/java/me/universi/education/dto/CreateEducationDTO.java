package me.universi.education.dto;

import java.util.Date;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CreateEducationDTO(
    @NotNull
    String educationType,

    @NotNull
    UUID institution,

    @NotNull
    Date startDate,

    @Nullable
    Date endDate
) {}
