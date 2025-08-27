package me.universi.group.DTO;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for updating an existing Group" )
public record UpdateGroupDTO(
        @NotBlank
        @JsonAlias( { "groupPath", "groupId" } )
        @Schema( description = "The edited Group ID or path" )
        String group,

        @Schema( description = "The new Group name" )
        Optional<String> name,
        @Schema( description = "ImageMetadata ID used for the new Group image" )
        Optional<UUID> image,
        @Schema( description = "ImageMetadata ID used for the new Group banner image" )
        Optional<UUID> bannerImage,
        @Schema( description = "ImageMetadata ID used for the new Group header image" )
        Optional<UUID> headerImage,

        @Schema( description = "HTML text describing this Group" )
        Optional<String> description,

        @JsonAlias( { "groupType" } )
        @Schema( description = "This Group's GroupType by ID or name" )
        Optional<String> type,

        @Schema( description = "If true, the Group will be able to hold subgroups" )
        Optional<Boolean> canCreateSubgroup,
        @Schema( description = "If true, the Group will be visible to all users" )
        Optional<Boolean> isPublic,
        @Schema( description = "If true, any user may join the Group freely" )
        Optional<Boolean> canJoin
) { }
