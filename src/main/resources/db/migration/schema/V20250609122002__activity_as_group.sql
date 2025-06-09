CREATE FUNCTION get_activity_nickname( name TEXT ) RETURNS TEXT AS $$
DECLARE
    counting INT := 0;
    handled_nickname TEXT;
    numbered_nickname TEXT;

BEGIN
    handled_nickname := REGEXP_REPLACE( name, '\s', '_', 'g' );
    handled_nickname := LOWER( handled_nickname );
    handled_nickname := REGEXP_REPLACE( handled_nickname, '[^a-z0-9_.-]', '', 'g' );

    numbered_nickname := handled_nickname;

    WHILE numbered_nickname IN ( SELECT nickname FROM system_group.system_group ) LOOP
        counting := counting + 1;
        numbered_nickname := handled_nickname || '.' || counting;
    END LOOP;

    RETURN numbered_nickname;
END;
$$ LANGUAGE plpgsql;

ALTER TABLE system_group.system_group
    ADD COLUMN activity_id UUID REFERENCES activity.activity ( id );

DO $$
DECLARE
    entity RECORD;
    group_id UUID;
    group_settings_id UUID;
    role_id UUID;
    nickname TEXT;

BEGIN
FOR entity IN SELECT * FROM activity.activity LOOP
    group_id := UUID_GENERATE_V4();
    group_settings_id := UUID_GENERATE_V4();
    nickname := get_activity_nickname( entity.name );

    INSERT INTO system_group.group_settings ( id, deleted ) VALUES ( group_settings_id, false );

    INSERT INTO system_group.system_group
        ( id, nickname, name, description, profile_id, type, can_create_group, can_enter, can_add_participant, created_at, public_group, deleted, group_settings_id, image_metadata_id, banner_image_metadata_id, header_image_metadata_id, parent_group_id, activity_id )
    VALUES
        ( group_id, nickname, entity.name, entity.description, entity.author_id, 'PROJECT', false, false, false, CURRENT_TIMESTAMP, false, entity.deleted_at IS NOT NULL, group_settings_id, NULL, NULL, NULL, entity.group_id, entity.id );

    role_id := UUID_GENERATE_V4();

    INSERT INTO system_group."role"
        ( id, deleted, created, removed, "name", description, group_id, role_type, feed_permission, content_permission, group_permission, people_permission, competence_permission, job_permission, activity_permission )
    VALUES
        ( UUID_GENERATE_V4(), false, CURRENT_TIMESTAMP, NULL, 'Administrador', NULL, group_id, 'ADMINISTRATOR', 4, 4, 4, 4, 4, 4, 4 ),
        ( role_id, false, CURRENT_TIMESTAMP, NULL, 'Participante', NULL, group_id, 'PARTICIPANT', 2, 2, 2, 2, 2, 2, 2 ),
        ( UUID_GENERATE_V4(), false, CURRENT_TIMESTAMP, NULL, 'Visitante', NULL, group_id, 'VISITOR', 2, 2, 2, 2, 2, 2, 2 );

    INSERT INTO system_group.profile_group
        ( group_id, profile_id, joined, id, exited, deleted, role_id )
    SELECT
        group_id, ap.profile_id, ap.joined_at, UUID_GENERATE_V4(), ap.removed_at, ap.removed_at IS NOT NULL, role_id
    FROM activity.participant ap
    WHERE activity_id = entity.id;
END LOOP;
END $$;

ALTER TABLE activity.activity
    DROP COLUMN name,
    DROP COLUMN description,
    DROP COLUMN author_id,
    DROP COLUMN group_id;

DROP TABLE activity.participant;
DROP FUNCTION get_activity_nickname;
