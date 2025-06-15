package me.universi.group.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

public record UpdateEmailFilterDTO(
    @NotNull
    UUID groupEmailFilterId,

    Optional<String> email,
    Optional<Boolean> enabled,
    Optional<String> type
) { }
