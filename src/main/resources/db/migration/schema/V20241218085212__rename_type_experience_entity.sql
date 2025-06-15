ALTER TABLE experience
    RENAME COLUMN type_experience_id TO experience_type_id;

CREATE SCHEMA experience;
ALTER TABLE type_experience
    SET SCHEMA experience;

ALTER TABLE experience.type_experience
    RENAME TO experience_type;
