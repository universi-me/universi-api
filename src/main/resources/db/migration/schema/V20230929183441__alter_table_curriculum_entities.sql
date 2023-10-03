ALTER TABLE type_education
ADD CONSTRAINT unique_type_education_id UNIQUE (id);

ALTER TABLE institution
ADD CONSTRAINT unique_institution_id UNIQUE (id);

ALTER TABLE education
ADD CONSTRAINT unique_education_id UNIQUE (id);

ALTER TABLE profile_experience
ADD CONSTRAINT unique_profile_experience_id UNIQUE (id);

ALTER TABLE type_experience
ADD CONSTRAINT unique_type_experience_id UNIQUE (id);

