package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Request body for creating a new Group" )
public record CreateGroupDTO(
        @JsonAlias( { "parentGroupId", "parentGroupPath" } )
        @Schema( description = "Parent Group ID or path" )
        Optional<String> parentGroup,

        @NotBlank
        @Schema( description = "The new Group nickname, used in the path" )
        String nickname,

        @NotBlank
        @Schema( description = "The new Group name" )
        String name,

        @Schema( description = "ImageMetadata ID used for the new Group image" )
        Optional<UUID> image,
        @Schema( description = "ImageMetadata ID used for the new Group banner image" )
        Optional<UUID> bannerImage,
        @Schema( description = "ImageMetadata ID used for the new Group header image" )
        Optional<UUID> headerImage,

        @NotBlank
        @Schema( description = "HTML text describing this Group" )
        String description,

        @NotBlank
        @JsonAlias( { "groupType" } )
        @Schema( description = "This Group's GroupType by ID or name" )
        String type,

        @Schema( description = "If true, the Group will be able to hold subgroups" )
        boolean canCreateSubgroup,
        @Schema( description = "If true, the Group will be visible to all users" )
        boolean isPublic,
        @Schema( description = "If true, any user may join the Group freely" )
        boolean canJoin
) { }
