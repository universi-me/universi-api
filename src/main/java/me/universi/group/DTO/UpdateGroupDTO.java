package me.universi.group.DTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateGroupDTO(
        Boolean isRootGroup,

        UUID groupId,
        String groupPath,

        @NotNull
        @NotBlank
        String name,

        @Nullable
        UUID image,
        UUID bannerImage,
        UUID headerImage,

        String description,

        String groupType,

        Boolean canHaveSubgroup,
        Boolean isPublic,
        Boolean canJoin,

        Boolean everyoneCanPost
) { }
