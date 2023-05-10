CREATE SEQUENCE alternative_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE alternative
(
    id          int8         NOT NULL DEFAULT nextval('alternative_sequence'),
    title       varchar(512) NOT NULL,
    correct     BOOL                  DEFAULT FALSE,
    question_id int8         NOT NULL,

    CONSTRAINT alternative_pkey PRIMARY KEY (id),
    CONSTRAINT question_id_pkey FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);
