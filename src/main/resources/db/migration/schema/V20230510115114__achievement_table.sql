
CREATE TABLE achievement
(
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),
    icon            VARCHAR(255) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    description     VARCHAR(255) NOT NULL,

    CONSTRAINT pk_achievement PRIMARY KEY (id)
);

CREATE TABLE indicators_achievement
(
    achievement_id UUID NOT NULL,
    indicators_id  UUID NOT NULL,

    CONSTRAINT pk_indicators_achievement PRIMARY KEY (achievement_id, indicators_id),
    CONSTRAINT fk_indach_on_achievement FOREIGN KEY (achievement_id) REFERENCES achievement(id),
    CONSTRAINT fk_indach_on_indicators FOREIGN KEY (indicators_id) REFERENCES indicators(id)
);
