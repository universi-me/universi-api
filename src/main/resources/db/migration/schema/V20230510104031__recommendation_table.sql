CREATE SEQUENCE recommendation_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE recommendation
(
    id_recommendation  INT8 NOT NULL DEFAULT nextval('recommendation_sequence'),
    origin             INT8,
    destiny            INT8,
    id_competence_type INT8,
    description        TEXT,
    creation_date      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_recommendation PRIMARY KEY (id_recommendation)
);

ALTER TABLE recommendation
    ADD CONSTRAINT FK_RECOMMENDATION_ON_DESTINY FOREIGN KEY (destiny) REFERENCES profile (id_profile);

ALTER TABLE recommendation
    ADD CONSTRAINT FK_RECOMMENDATION_ON_ID_COMPETENCE_TYPE FOREIGN KEY (id_competence_type) REFERENCES competence_type (id_competence_type);

ALTER TABLE recommendation
    ADD CONSTRAINT FK_RECOMMENDATION_ON_ORIGIN FOREIGN KEY (origin) REFERENCES profile (id_profile);