package me.universi.activity.dto;

import java.util.Optional;

import me.universi.activity.enums.ActivityStatus;

public record FilterActivityDTO(
    Optional<String> type,
    Optional<String> group,
    Optional<ActivityStatus> status
) {}
