package me.universi.activity.dto;

import java.util.Optional;

public record FilterActivityDTO(
    Optional<String> type,
    Optional<String> group
) {}
