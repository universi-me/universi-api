-- remove present_date column
UPDATE experience
SET end_date = NULL
WHERE present_date IS TRUE;

ALTER TABLE experience
    DROP COLUMN present_date;

-- add profile to experience
ALTER TABLE experience
    ADD COLUMN profile_id UUID;

UPDATE experience e
SET profile_id = (
    SELECT ep.profile_id
    FROM experience_profile ep
    WHERE ep.experience_id = e.id
);

ALTER TABLE experience
    ADD CONSTRAINT fk_experience_profile FOREIGN KEY ( profile_id ) references profile (id),
    ALTER COLUMN profile_id SET NOT NULL;

DROP TABLE experience_profile;

-- set schema
ALTER TABLE experience
    SET SCHEMA experience;
