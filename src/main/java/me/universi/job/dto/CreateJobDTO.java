package me.universi.job.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateJobDTO(
    @NotNull
    String title,

    @NotNull
    String shortDescription,

    @NotNull
    String longDescription,

    @NotNull
    UUID institutionId,

    @NotNull
    List<UUID> requiredCompetencesIds
) { }
