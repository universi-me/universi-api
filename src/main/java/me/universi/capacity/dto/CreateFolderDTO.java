package me.universi.capacity.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for creating new Folders" )
public record CreateFolderDTO(
    @NotBlank
    String name,

    @Nullable
    @Schema( description = "ImageMetadata ID used for the Folder" )
    UUID image,

    @Nullable
    @Schema( description = "Short plain text describing this Folder" )
    String description,

    @NotNull
    @Min(0) @Max(5)
    @Schema( description = "A rating of this Folder's quality" )
    Integer rating,

    @Schema( description = "If true sets this Folder to public, otherwise sets it to non-public/private" )
    Optional<Boolean> publicFolder,

    @Nullable
    @Schema( description = "All Categories this Folder matches" )
    List<UUID> categoriesIds,

    @Nullable
    @Schema( description = "A list of Groups IDs or paths to put this Folder in" )
    List<String> grantedAccessGroupsIds,

    @Nullable
    @Schema( description = "A list of CompetenceTypes IDs to grant a badge on this Folder completion" )
    List<UUID> competenceTypeBadgeIds
) { }
