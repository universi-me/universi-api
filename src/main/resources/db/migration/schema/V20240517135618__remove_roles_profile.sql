DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'roles_profile') THEN

ALTER TABLE profile_group
    ADD COLUMN role_id UUID REFERENCES roles (id);

UPDATE profile_group
SET role_id = (
    SELECT roles_id
    FROM roles_profile rp
    WHERE rp.profile_id = profile_id AND rp.group_id = group_id AND rp.deleted = false LIMIT 1
    )
WHERE role_id IS NULL;

UPDATE profile_group pg
SET role_id = (
    SELECT r.id
    FROM roles r
    WHERE r.group_id = pg.group_id
      AND r.role_type = CASE WHEN (
                                      SELECT su.authority
                                      FROM system_users su
                                               INNER JOIN profile p ON p.user_id = su.id
                                      WHERE pg.profile_id = p.id
                                  ) = 'ROLE_ADMIN' THEN 'ADMINISTRATOR' ELSE 'PARTICIPANT'
        END
)
WHERE pg.role_id IS NULL;

ALTER TABLE profile_group
    ALTER COLUMN role_id SET NOT NULL;

DROP TABLE roles_profile;

END IF;
END $$;
