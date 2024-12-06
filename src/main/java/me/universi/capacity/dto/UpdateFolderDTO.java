package me.universi.capacity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;

public record UpdateFolderDTO(
    @Nullable
    String name,

    @Nullable
    String image,

    @Nullable
    String description,

    @Nullable
    @Min( 0 ) @Max( 5 )
    Integer rating,

    @Nullable
    Boolean publicFolder,

    @Nullable
    List<UUID> categoriesIds,

    @Nullable
    List<String> grantedAccessGroups,

    @Nullable
    List<UUID> competenceTypeBadgeIds
) { }
