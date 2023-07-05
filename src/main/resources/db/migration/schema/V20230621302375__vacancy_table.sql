CREATE SEQUENCE  IF NOT EXISTS vacancy_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE vacancy (
	id_vacancy int8 NOT NULL DEFAULT nextval('vacancy_sequence'),
	id_profile int8 NULL,
	description TEXT NULL,
	creation_date timestamp(6) NULL,
	CONSTRAINT pk_vacancy PRIMARY KEY (id_vacancy)
);

ALTER TABLE vacancy ADD CONSTRAINT FK_VACANCY_ON_ID_PROFILE FOREIGN KEY (id_profile) REFERENCES profile (id_profile);