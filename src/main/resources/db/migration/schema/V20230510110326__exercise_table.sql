CREATE SEQUENCE exercise_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE exercise
(
    id       INT8 NOT NULL DEFAULT nextval('exercise_sequence'),
    title    VARCHAR(255) NOT NULL,
    group_id INT8       NOT NULL,
    CONSTRAINT pk_exercise PRIMARY KEY (id)
);

ALTER TABLE exercise
    ADD CONSTRAINT FK_EXERCISE_ON_GROUP FOREIGN KEY (group_id) REFERENCES system_group (id_group);
