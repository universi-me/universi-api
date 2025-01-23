package me.universi.job.dto;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;

public record UpdateJobDTO(
    @Nullable
    String title,

    @Nullable
    String shortDescription,

    @Nullable
    String longDescription,

    @Nullable
    UUID institutionId,

    @Nullable
    List<UUID> requiredCompetencesIds
) { }
