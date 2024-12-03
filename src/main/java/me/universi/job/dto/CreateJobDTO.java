package me.universi.job.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateJobDTO(
    @NotNull( message = "O parâmetro 'title' não foi informado" )
    @NotBlank( message = "O parâmetro 'title' não pode estar vazio" )
    String title,

    @NotNull( message = "O parâmetro 'shortDescription' não foi informado" )
    @NotBlank( message = "O parâmetro 'shortDescription' não pode estar vazio" )
    String shortDescription,

    @NotNull( message = "O parâmetro 'longDescription' não foi informado" )
    @NotBlank( message = "O parâmetro 'longDescription' não pode estar vazio" )
    String longDescription,

    @NotNull( message = "O parâmetro 'institutionId' não foi informado" )
    UUID institutionId,

    @NotNull( message = "O parâmetro 'requiredCompetencesIds' não foi informado" )
    List<UUID> requiredCompetencesIds
) { }
