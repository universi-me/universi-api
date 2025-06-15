ALTER TABLE activity.activity
    ADD COLUMN group_id UUID NOT NULL REFERENCES system_group.system_group ( id );
