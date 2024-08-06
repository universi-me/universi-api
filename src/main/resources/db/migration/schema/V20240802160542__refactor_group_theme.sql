ALTER TABLE group_theme
    -- remove unused colors
    DROP COLUMN font_color_v6,
    DROP COLUMN button_yellow_hover_color,
    DROP COLUMN forms_color,
    DROP COLUMN tertiary_color,

    DROP COLUMN wave_color,     -- using secondary-color instead
    DROP COLUMN rank_color,     -- using secondary-color instead
    DROP COLUMN skills_1_color, -- using secondary-color instead
    DROP COLUMN font_color_v5;  -- using card-background-item instead

-- renaming colors
ALTER TABLE group_theme RENAME COLUMN font_disabled_color TO font_color_disabled;

ALTER TABLE group_theme RENAME COLUMN alert_color TO font_color_alert;

ALTER TABLE group_theme RENAME COLUMN success_color TO font_color_success;

ALTER TABLE group_theme RENAME COLUMN font_color_v4 TO font_color_links;
