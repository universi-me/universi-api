CREATE SEQUENCE competence_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE competence
(
    id_competence      INT8 NOT NULL DEFAULT nextval('competence_sequence'),
    id_competence_type INT8,
    id_profile         INT8,
    description        TEXT,
    level              VARCHAR(255),
    creation_date      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_competence PRIMARY KEY (id_competence)
);

ALTER TABLE competence
    ADD CONSTRAINT FK_COMPETENCE_ON_ID_COMPETENCE_TYPE FOREIGN KEY (id_competence_type) REFERENCES competence_type (id_competence_type);

ALTER TABLE competence
    ADD CONSTRAINT FK_COMPETENCE_ON_ID_PROFILE FOREIGN KEY (id_profile) REFERENCES profile (id_profile);