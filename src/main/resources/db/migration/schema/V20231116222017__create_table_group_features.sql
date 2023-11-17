
CREATE TABLE group_features
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    group_settings_id   UUID NOT NULL,

    contents            BOOLEAN NOT NULL DEFAULT TRUE,
    groups              BOOLEAN NOT NULL DEFAULT TRUE,
    participants        BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_group_features PRIMARY KEY (id)
);

CREATE TABLE group_theme
(
    id                          UUID NOT NULL DEFAULT uuid_generate_v4(),
    deleted                     BOOLEAN NOT NULL DEFAULT FALSE,
    group_settings_id           UUID NOT NULL,

    primary_color               VARCHAR(10),
    secondary_color             VARCHAR(10),
    tertiary_color              VARCHAR(10),
    background_color            VARCHAR(10),
    card_background_color       VARCHAR(10),
    card_item_color             VARCHAR(10),
    font_color_v1               VARCHAR(10),
    font_color_v2               VARCHAR(10),
    font_color_v3               VARCHAR(10),
    font_color_v4               VARCHAR(10),
    font_color_v5               VARCHAR(10),
    font_color_v6               VARCHAR(10),
    font_disabled_color         VARCHAR(10),
    forms_color                 VARCHAR(10),
    skills_1_color              VARCHAR(10),
    wave_color                  VARCHAR(10),
    button_yellow_hover_color   VARCHAR(10),
    button_hover_color          VARCHAR(10),
    alert_color                 VARCHAR(10),
    success_color               VARCHAR(10),
    wrong_invalid_color         VARCHAR(10),
    rank_color                  VARCHAR(10),

    CONSTRAINT pk_group_theme PRIMARY KEY (id)
);
