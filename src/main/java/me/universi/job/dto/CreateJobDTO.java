package me.universi.job.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateJobDTO(
    @NotBlank
    String title,

    @NotBlank
    String shortDescription,

    @NotBlank
    String longDescription,

    @NotNull
    UUID institutionId,

    @NotNull
    List<String> requiredCompetencesIds
) { }
