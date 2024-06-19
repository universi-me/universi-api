ALTER TABLE roles
    ADD COLUMN job_permission INT;

UPDATE roles
    set job_permission = CASE
        WHEN role_type = 'ADMINISTRATOR' THEN 4
        ELSE 2
    END;

ALTER TABLE roles
    ALTER COLUMN job_permission SET NOT NULL;
