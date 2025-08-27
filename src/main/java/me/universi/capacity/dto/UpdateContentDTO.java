package me.universi.capacity.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.enums.ContentType;

@Schema( description = "Request body for updating existing Contents" )
public record UpdateContentDTO(
    Optional<String> url,
    Optional<String> title,
    Optional<ContentType> type,
    Optional<UUID> image,
    Optional<String> description,

    Optional<
        @Min( Content.MIN_RATING ) @Max( Content.MAX_RATING )
        Integer
    > rating,

    @JsonAlias( { "categoriesIds" } )
    Optional<List<String>> categories
) { }
