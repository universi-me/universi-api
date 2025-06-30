package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CreateGroupDTO(
        @JsonAlias( { "parentGroupId", "parentGroupPath" } )
        Optional<String> parentGroup,

        @NotBlank
        String nickname,

        @NotBlank
        String name,

        Optional<UUID> image,
        Optional<UUID> bannerImage,
        Optional<UUID> headerImage,

        @NotBlank
        String description,

        @NotBlank
        @JsonAlias( { "groupType" } )
        String type,

        boolean canCreateSubgroup,
        boolean isPublic,
        boolean canJoin
) { }
