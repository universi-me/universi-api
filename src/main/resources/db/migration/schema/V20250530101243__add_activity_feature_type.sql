ALTER TABLE system_group.role
    ADD COLUMN activity_permission INT;

UPDATE system_group.role
    SET activity_permission = CASE
        WHEN role_type = 'ADMINISTRATOR' THEN 4
        ELSE 2
    END;

ALTER TABLE system_group.role
    ALTER COLUMN activity_permission SET NOT NULL;
