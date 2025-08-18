package me.universi.activity.dto;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema( description = "Request body for updating existing Activities" )
public record UpdateActivityDTO(
    Optional<String> type,
    @Schema( description = "Human-readable description of where and when this Activity takes place" )
    Optional<String> location,
    @Schema( description = "How many hours long this Activity lasts" )
    Optional<Integer> workload,
    @Schema( description = "All CompetenceType badges this Activity grants, by ID or name" )
    Optional<List<@NotBlank String>> badges,

    @JsonAlias( { "start" } )
    @Schema( description = "Start date for this Activity" )
    Optional<Date> startDate,

    @JsonAlias( { "end" } )
    @Schema( description = "End date for this Activity. If it happens during one day only, set it to equal `startDate`" )
    Optional<Date> endDate,

    @Schema( description = "Name of the related Activity Group" )
    Optional<String> name,
    @Schema( description = "Description of the related Activity Group" )
    Optional<String> description,
    @Schema( description = "ImageMetadata ID used for the Activity Group image" )
    Optional<UUID> image,
    @Schema( description = "ImageMetadata ID used for the Activity Group imageBanner" )
    Optional<UUID> bannerImage
) {}
