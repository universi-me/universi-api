CREATE SEQUENCE competence_type_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE competence_type
(
    id_competence_type INT8 NOT NULL DEFAULT nextval('competence_type_sequence'),
    name                VARCHAR(255),
    CONSTRAINT pk_competence_type PRIMARY KEY (id_competence_type)
);

ALTER TABLE competence_type
    ADD CONSTRAINT uc_competence_type_name UNIQUE (name);