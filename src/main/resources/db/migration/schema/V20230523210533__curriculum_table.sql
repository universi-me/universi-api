
CREATE TABLE curriculum (
	id             UUID NOT NULL DEFAULT uuid_generate_v4(),
	created_at     TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
	description    text NULL,
	id_profile     UUID NOT NULL,

	CONSTRAINT curriculum_pkey PRIMARY KEY (id),
	CONSTRAINT uk_id_profile_curriculum UNIQUE (id_profile),
	CONSTRAINT pk_id_profile_curriculum FOREIGN KEY (id_profile) REFERENCES profile(id)
);