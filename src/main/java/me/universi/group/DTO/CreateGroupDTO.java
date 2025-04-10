package me.universi.group.DTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateGroupDTO(
        Boolean groupRoot,

        UUID parentGroupId,
        String groupPath,

        @NotNull
        String nickname,

        @NotNull
        String name,

        @Nullable
        UUID image,
        UUID bannerImage,
        UUID headerImage,

        String description,

        @NotNull
        String groupType,

        Boolean canCreateGroup,
        Boolean isPublic,
        Boolean canJoin,
        Boolean everyoneCanPost
) { }
