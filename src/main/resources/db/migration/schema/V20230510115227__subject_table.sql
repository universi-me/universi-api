CREATE SEQUENCE subject_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE subject
(
    id      INT8 NOT NULL DEFAULT nextval('subject_sequence'),
    subject VARCHAR(255),
    CONSTRAINT pk_subject PRIMARY KEY (id)
);