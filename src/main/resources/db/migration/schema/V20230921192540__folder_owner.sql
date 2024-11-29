
ALTER TABLE folder
    ADD COLUMN owner_group_id UUID
    REFERENCES system_group (id);

UPDATE folder
    SET owner_group_id = (
        SELECT g.id
        FROM system_group g
        WHERE g.group_root
        ORDER BY g.created_at ASC
        LIMIT 1
    )
    WHERE owner_group_id IS NULL;

ALTER TABLE folder
    ALTER COLUMN owner_group_id SET NOT NULL;
