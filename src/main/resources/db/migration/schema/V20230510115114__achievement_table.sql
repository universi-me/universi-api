CREATE SEQUENCE achievement_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE achievement
(
    id          INT8 NOT NULL DEFAULT nextval('achievement_sequence'),
    icon        VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT pk_achievement PRIMARY KEY (id)
);

CREATE TABLE indicators_achievement
(
    achievement_id INT8 NOT NULL,
    indicators_id  INT8 NOT NULL,
    CONSTRAINT pk_indicators_achievement PRIMARY KEY (achievement_id, indicators_id)
);

ALTER TABLE indicators_achievement
    ADD CONSTRAINT fk_indach_on_achievement FOREIGN KEY (achievement_id) REFERENCES achievement (id);

ALTER TABLE indicators_achievement
    ADD CONSTRAINT fk_indach_on_indicators FOREIGN KEY (indicators_id) REFERENCES indicators (id);