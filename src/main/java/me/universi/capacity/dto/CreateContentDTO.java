package me.universi.capacity.dto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.enums.ContentType;

@Schema( description = "Request body for creating new Contents" )
public record CreateContentDTO(
    @Schema( description = "Direct URL of the content material" )
    @NotBlank
    String url,

    @NotBlank
    String title,

    @NotNull
    @Schema( description = "The type of the Content" )
    ContentType type,

    @Schema( description = "ImageMetadata ID used for the Content" )
    Optional<UUID> image,

    @Schema( description = "Short plain text describing this Content" )
    Optional<String> description,

    @NotNull
    @Min( Content.MIN_RATING ) @Max( Content.MAX_RATING )
    @Schema( description = "A rating of this content's quality" )
    Integer rating,

    @JsonAlias( { "categoriesIds" } )
    @Schema( description = "All Categories this Content matches" )
    Optional<List<String>> categories,

    @JsonAlias( { "foldersIds" } )
    @Schema( description = "All folders this content should be added on creation" )
    Optional<List<String>> folders
) { }
