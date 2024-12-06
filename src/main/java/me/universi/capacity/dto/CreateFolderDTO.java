package me.universi.capacity.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateFolderDTO(
    @NotNull( message = "O parâmetro 'name' não foi informado" )
    @NotBlank( message = "O parâmetro 'name' não pode estar vazio" )
    String name,

    @Nullable
    String image,

    @Nullable
    String description,

    @NotNull( message = "O parâmetro 'rating' não foi informado" )
    @Min(0) @Max(5)
    Integer rating,

    @Nullable
    boolean publicFolder,

    @Nullable
    List<UUID> categoriesIds,

    @Nullable
    List<String> grantedAccessGroupsIds,

    @Nullable
    List<UUID> competenceTypeBadgeIds
) { }
