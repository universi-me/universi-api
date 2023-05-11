CREATE SEQUENCE feedback_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE feedback
(
    id            int8         NOT NULL DEFAULT nextval('feedback_sequence'),
    link          VARCHAR(256) NOT NULL,
    feedback_text VARCHAR(512) NOT NULL,
    CONSTRAINT feedback_pkey PRIMARY KEY (id)
);