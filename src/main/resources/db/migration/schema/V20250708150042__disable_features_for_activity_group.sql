ALTER TABLE system_group.role
    DROP COLUMN activity_permission;

UPDATE system_group.role
SET
    group_permission = 1,
    job_permission = 1
WHERE group_id IN (
    SELECT sg.id
    FROM system_group.system_group sg
    WHERE sg.activity_id IS NOT NULL
);
