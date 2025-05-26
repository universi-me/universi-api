package me.universi.job.dto;

import java.util.List;
import java.util.Optional;

public record UpdateJobDTO(
    Optional<String> title,

    Optional<String> shortDescription,

    Optional<String> longDescription,

    Optional<List<String>> requiredCompetencesIds
) { }
