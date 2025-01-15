package me.universi.group.DTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateGroupDTO(
        Boolean groupRoot,

        UUID groupId,
        String groupPath,

        @NotNull
        @NotBlank
        String nickname,

        @NotNull
        @NotBlank
        String name,

        @Nullable
        UUID image,
        UUID bannerImage,
        UUID headerImage,

        String description,

        @NotNull
        @NotBlank
        String type,

        Boolean canCreateGroup,
        Boolean publicGroup,
        Boolean canEnter
) { }
