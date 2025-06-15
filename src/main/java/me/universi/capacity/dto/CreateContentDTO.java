package me.universi.capacity.dto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.enums.ContentType;

public record CreateContentDTO(
    @NotBlank
    String url,

    @NotBlank
    String title,

    @NotNull
    ContentType type,

    Optional<UUID> image,

    Optional<String> description,

    @NotNull
    @Min( Content.MIN_RATING ) @Max( Content.MAX_RATING )
    Integer rating,

    @JsonAlias( { "categoriesIds" } )
    Optional<List<String>> categories,

    @JsonAlias( { "foldersIds" } )
    Optional<List<String>> folders
) { }
