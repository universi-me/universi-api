ALTER TABLE competence_profile
    ADD id UUID;

UPDATE competence_profile
    SET id = uuid_generate_v4();

ALTER TABLE competence_profile
    DROP CONSTRAINT pk_competence_profile;

ALTER TABLE competence_profile
    ALTER COLUMN id SET NOT NULL,
    ADD PRIMARY KEY (id);
