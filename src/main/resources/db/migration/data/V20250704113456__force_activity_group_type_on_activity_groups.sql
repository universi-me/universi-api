DO $$
DECLARE
    activity_group_type UUID;

BEGIN
    activity_group_type := (
        SELECT id
        FROM system_group.type
        WHERE kind = 'ACTIVITY'
        LIMIT 1
    );

    UPDATE system_group.system_group
        SET type_id = activity_group_type
        WHERE activity_id IS NOT NULL;
END $$;
