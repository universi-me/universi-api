package me.universi.activity.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AdditionalPropertiesValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.role.enums.FeaturesTypes;
import me.universi.role.openapi.FeatureToLevelSchema;

@Schema( description = "Request body for creating new Activities" )
public record CreateActivityDTO(
    @Schema( description = "Name of the related Activity Group" )
    @NotBlank String name,

    @Schema( description = "Description of the related Activity Group" )
    @NotBlank String description,

    @Schema( description = "ActivityType to use, by ID or name" )
    @NotBlank String type,

    @Schema( description = "Human-readable description of where and when this Activity takes place" )
    @NotBlank String location,

    @Schema( description = "How many hours long this Activity lasts" )
    Optional<Integer> workload,

    @Schema( description = "All CompetenceType badges this Activity grants, by ID or name" )
    Optional<List<@NotBlank String>> badges,

    @Schema( description = "ImageMetadata ID used for the Activity Group image" )
    Optional<UUID> image,

    @Schema( description = "ImageMetadata ID used for the Activity Group imageBanner" )
    Optional<UUID> bannerImage,

    @JsonAlias( { "groupId", "groupPath" } )
    @Schema( description = "Parent group for this Activity Group, by ID or path" )
    @NotBlank String group,

    @JsonAlias( { "start" } )
    @Schema( description = "Start date for this Activity" )
    @NotNull Date startDate,

    @JsonAlias( { "end" } )
    @Schema( description = "End date for this Activity. If it happens during one day only, set it to equal `startDate`" )
    @NotNull Date endDate,

    @JsonAlias( { "rolesFeatures", "rolesConfig" } )
    @Schema( description = "Configuration settings for this Activity Group permissions" )
    Optional<CreateActivityRoleConfigDTO> features
) {
    public record CreateActivityRoleConfigDTO(
        @JsonAlias( { "admin" } )
        @Schema( description = "Configures permission level for each feature for Group administrators", ref = FeatureToLevelSchema.REF_STRING, additionalProperties = AdditionalPropertiesValue.FALSE )
        Optional<Map<FeaturesTypes, Integer>> administrator,

        @JsonAlias( { "member" } )
        @Schema( description = "Configures permission level for each feature for Group participants", ref = FeatureToLevelSchema.REF_STRING, additionalProperties = AdditionalPropertiesValue.FALSE )
        Optional<Map<FeaturesTypes, Integer>> participant,

        @Schema( description = "Configures permission level for each feature for Group visitors", ref = FeatureToLevelSchema.REF_STRING, additionalProperties = AdditionalPropertiesValue.FALSE )
        Optional<Map<FeaturesTypes, Integer>> visitor
    ) {}
}
