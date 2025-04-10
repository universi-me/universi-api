package me.universi.group.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UpdateGroupThemeDTO(
        @NotNull
        UUID groupId,

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
