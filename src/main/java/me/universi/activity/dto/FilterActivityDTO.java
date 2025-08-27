package me.universi.activity.dto;

import java.util.Date;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import me.universi.activity.enums.ActivityStatus;

@Schema( description = "Optional search parameters to filter Activities" )
public record FilterActivityDTO(
    @Schema( description = "Only show activities with specified ActivityType, by ID or Name" )
    Optional<String> type,

    @Schema( description = "Only show activities happening in specified Group, by ID or path" )
    Optional<String> group,

    @Schema( description = "Only show activities with specified ActivityStatus" )
    Optional<ActivityStatus> status,

    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE )
    @Schema( description = "Only show activities starting on this date or later" )
    Optional<Date> startDate,

    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE )
    @Schema( description = "Only show activities ending on this date or before" )
    Optional<Date> endDate
) {}
