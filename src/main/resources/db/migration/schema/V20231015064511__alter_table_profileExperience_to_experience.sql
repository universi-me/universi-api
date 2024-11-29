ALTER TABLE profile_experience RENAME TO experience;

ALTER TABLE experience DROP COLUMN profile_id;

CREATE TABLE experience_profile (
    profile_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    experience_id UUID NOT NULL DEFAULT uuid_generate_v4(),

    CONSTRAINT pk_experience_profile PRIMARY KEY (profile_id, experience_id),
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES profile(id),
    CONSTRAINT fk_experience_id FOREIGN KEY (experience_id) REFERENCES experience(id)
);
