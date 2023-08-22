
CREATE TABLE vacancy (
	id            UUID NOT NULL DEFAULT uuid_generate_v4(),
	id_profile    UUID NULL,
	description   TEXT NULL,
	creation_date timestamp(6) NULL,

	CONSTRAINT pk_vacancy PRIMARY KEY (id),
	CONSTRAINT FK_VACANCY_ON_ID_PROFILE FOREIGN KEY (id_profile) REFERENCES profile(id)
);