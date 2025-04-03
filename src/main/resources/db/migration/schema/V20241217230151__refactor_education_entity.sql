-- rename education_type column
ALTER TABLE education
    RENAME COLUMN type_education_id TO education_type_id;

-- remove present_date column
UPDATE education
    SET end_date = NULL
WHERE present_date IS TRUE;

ALTER TABLE education
    DROP COLUMN present_date;

-- add profile to education
ALTER TABLE education
    ADD COLUMN profile_id UUID;

UPDATE education e
SET profile_id = (
    SELECT ep.profile_id
    FROM education_profile ep
    WHERE ep.education_id = e.id
);

ALTER TABLE education
    ADD CONSTRAINT fk_education_profile FOREIGN KEY ( profile_id ) references profile (id),
    ALTER COLUMN profile_id SET NOT NULL;

-- drop education_profile
DROP TABLE education_profile;

-- add schema
ALTER TABLE education
    SET SCHEMA education;
