package me.universi.role.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AdditionalPropertiesValue;
import jakarta.annotation.Nullable;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.openapi.FeatureToLevelSchema;

@Schema( description = "Request body for updating Roles in a Group" )
public record UpdateRoleDTO(
    @Nullable
    @Schema( description = "New name for the edited Role", examples = { "Leader", } )
    String name,

    @Nullable
    @Schema( description = "New description for the edited Role", deprecated = true )
    String description,

    @Nullable
    @Schema( deprecated = true )
    String group,

    @Nullable
    @Schema( description = "The new Permissions of this Role", ref = FeatureToLevelSchema.REF_STRING, additionalProperties = AdditionalPropertiesValue.FALSE )
    Map<FeaturesTypes, Integer> features
) {}
