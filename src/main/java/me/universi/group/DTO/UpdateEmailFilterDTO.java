package me.universi.group.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Request body for updating GroupEmailFilters" )
public record UpdateEmailFilterDTO(
    @NotNull
    @Schema( description = "The ID of the updated GroupEmailFilter" )
    UUID groupEmailFilterId,

    Optional<String> email,
    Optional<Boolean> enabled,
    Optional<String> type
) { }
