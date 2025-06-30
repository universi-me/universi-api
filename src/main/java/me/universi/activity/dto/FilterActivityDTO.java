package me.universi.activity.dto;

import java.util.Date;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;

import me.universi.activity.enums.ActivityStatus;

public record FilterActivityDTO(
    Optional<String> type,
    Optional<String> group,
    Optional<ActivityStatus> status,

    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE )
    Optional<Date> startDate,

    @DateTimeFormat( iso = DateTimeFormat.ISO.DATE )
    Optional<Date> endDate
) {}
