package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateGroupDTO(
        Boolean groupRoot,

        UUID groupId,
        String groupPath,

        @NotNull
        @NotBlank
        String nickname,

        @NotNull
        @NotBlank
        String name,

        String imageUrl,
        String bannerImageUrl,
        String headerImageUrl,

        String description,

        @NotNull
        @NotBlank
        String type,

        Boolean canCreateGroup,
        Boolean publicGroup,
        Boolean canEnter,

        Boolean everyoneCanPost
) { }
