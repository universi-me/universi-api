ALTER TABLE system_group.system_group
    ADD COLUMN parent_group_id UUID REFERENCES system_group.system_group( id );

UPDATE system_group.system_group sg
SET parent_group_id = (
    SELECT sub.group_id
    FROM system_group.subgroup sub
    WHERE sub.subgroup_id = sg.id
)
WHERE sg.parent_group_id IS NULL;

DROP TABLE system_group.subgroup;
