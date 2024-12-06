package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import me.universi.capacity.enums.ContentType;

public record UpdateContentDTO(
    @Nullable
    String url,

    @Nullable
    String title,

    @Nullable
    ContentType type,

    @Nullable
    String image,

    @Nullable
    String description,

    @Nullable
    @Min( 0 ) @Max( 5 )
    Integer rating,

    @Nullable
    List<UUID> categoriesIds
) { }
