package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.enums.ContentType;

public record CreateContentDTO(
    @NotNull
    String url,

    @NotNull
    String title,

    @NotNull
    ContentType type,

    @Nullable
    UUID image,

    @Nullable
    String description,

    @NotNull
    @Min( 0 ) @Max( 5 )
    Integer rating,

    @Nullable
    List<UUID> categoriesIds,

    @Nullable
    List<String> folders
) { }
