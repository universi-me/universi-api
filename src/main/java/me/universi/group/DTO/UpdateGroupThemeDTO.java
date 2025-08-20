package me.universi.group.DTO;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonAlias;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( description = "Request body for updating the organization GroupTheme. Each key other than `group` is a CSS color to be used on the Web Client" )
public record UpdateGroupThemeDTO(
        @NotBlank
        @JsonAlias( { "groupId", "groupPath" } )
        @Schema( description = "The organization Group ID or path" )
        String group,

        String primary_color,
        String secondary_color,
        String background_color,
        String card_background_color,
        String card_item_color,
        String font_color_v1,
        String font_color_v2,
        String font_color_v3,
        String font_color_links,
        String font_color_disabled,
        String button_hover_color,
        String font_color_alert,
        String font_color_success,
        String wrong_invalid_color
) { }
