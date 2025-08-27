package me.universi.activity.dto;

import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Request body for updating existing ActivityTypes" )
public record UpdateActivityTypeDTO(
    Optional<String> name
) { }
