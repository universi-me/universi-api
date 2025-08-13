package me.universi.capacity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@Schema( description = "Request body for updating existing Folders" )
public record UpdateFolderDTO(
    @Nullable
    String name,

    @Nullable
    @Schema( description = "ImageMetadata ID used for the Folder" )
    UUID image,

    @Nullable
    @Schema( description = "Short plain text describing this Folder" )
    String description,

    @Nullable
    @Min( 0 ) @Max( 5 )
    @Schema( description = "A rating of this Folder's quality" )
    Integer rating,

    @Nullable
    @Schema( description = "If true sets this Folder to public, or, if false, sets it to non-public/private" )
    Boolean publicFolder,

    @Nullable
    @Schema( description = "All Categories this Folder matches" )
    List<UUID> categoriesIds,

    @Schema( description = "A list of Groups IDs or paths to put this Folder in. Removes it from its current Groups" )
    Optional<List<String>> grantedAccessGroups,

    @Schema( description = "A list of Groups IDs or paths to put this Folder in, adding to the current Groups" )
    Optional<List<String>> addGrantedAccessGroups,

    @Schema( description = "A list of Groups IDs or paths to remove this Folder from" )
    Optional<List<String>> removeGrantedAccessGroups,

    @Nullable
    @Schema( description = "A list of CompetenceTypes IDs to grant a badge on this Folder completion" )
    List<UUID> competenceTypeBadgeIds
) { }
