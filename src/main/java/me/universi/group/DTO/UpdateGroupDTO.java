package me.universi.group.DTO;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public record UpdateGroupDTO(
        @NotBlank
        @JsonAlias( { "groupPath", "groupId" } )
        String group,

        Optional<String> name,
        Optional<UUID> image,
        Optional<UUID> bannerImage,
        Optional<UUID> headerImage,

        Optional<String> description,

        Optional<String> groupType,

        Optional<Boolean> canCreateSubgroup,
        Optional<Boolean> isPublic,
        Optional<Boolean> canJoin,
        Optional<Boolean> everyoneCanPost
) { }
