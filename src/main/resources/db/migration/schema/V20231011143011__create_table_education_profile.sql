ALTER TABLE education DROP COLUMN profile_id;

CREATE TABLE education_profile (
    profile_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    education_id UUID NOT NULL DEFAULT uuid_generate_v4(),

    CONSTRAINT pk_education_profile PRIMARY KEY (profile_id, education_id),
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES profile(id),
    CONSTRAINT fk_education_id FOREIGN KEY (education_id) REFERENCES education(id)
);