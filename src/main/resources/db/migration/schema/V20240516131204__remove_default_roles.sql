ALTER TABLE roles
    ADD COLUMN role_type TEXT CHECK (role_type IN ('ADMINISTRATOR', 'PARTICIPANT', 'VISITOR', 'CUSTOM'));

CREATE UNIQUE INDEX unique_index_role_type ON roles (group_id, role_type)
    WHERE role_type IN ('ADMINISTRATOR', 'PARTICIPANT', 'VISITOR');

UPDATE roles
    SET role_type = 'CUSTOM'
    WHERE role_type IS NULL;

INSERT INTO roles (removed, "name", description, group_id, role_type)
    SELECT NULL AS "removed", 'Administrador' AS "name", '' AS "description", id AS group_id, 'ADMINISTRATOR' AS role_type FROM system_group sg;

INSERT INTO roles (removed, "name", description, group_id, role_type)
    SELECT NULL AS "removed", 'Participante' AS "name", '' AS "description", id AS group_id, 'PARTICIPANT' AS role_type FROM system_group sg;

INSERT INTO roles (removed, "name", description, group_id, role_type)
    SELECT NULL AS "removed", 'Visitante' AS "name", '' AS "description", id AS group_id, 'VISITOR' AS role_type FROM system_group sg;

INSERT INTO roles_feature (removed, roles_id, feature, "permission")
    SELECT NULL AS "removed", r.id AS roles_id, 'FEED' AS "feature", CASE WHEN r.role_type = 'ADMINISTRATOR' THEN 4 ELSE 2 END AS "permission"
    FROM roles r
    WHERE r.role_type IN ('ADMINISTRATOR', 'PARTICIPANT', 'VISITOR');
INSERT INTO roles_feature (removed, roles_id, feature, "permission")
    SELECT NULL AS "removed", r.id AS roles_id, 'CONTENT' AS "feature", CASE WHEN r.role_type = 'ADMINISTRATOR' THEN 4 ELSE 2 END AS "permission"
    FROM roles r
    WHERE r.role_type IN ('ADMINISTRATOR', 'PARTICIPANT', 'VISITOR');
INSERT INTO roles_feature (removed, roles_id, feature, "permission")
    SELECT NULL AS "removed", r.id AS roles_id, 'GROUP' AS "feature", CASE WHEN r.role_type = 'ADMINISTRATOR' THEN 4 ELSE 2 END AS "permission"
    FROM roles r
    WHERE r.role_type IN ('ADMINISTRATOR', 'PARTICIPANT', 'VISITOR');
INSERT INTO roles_feature (removed, roles_id, feature, "permission")
    SELECT NULL AS "removed", r.id AS roles_id, 'PEOPLE' AS "feature", CASE WHEN r.role_type = 'ADMINISTRATOR' THEN 4 ELSE 2 END AS "permission"
    FROM roles r
    WHERE r.role_type IN ('ADMINISTRATOR', 'PARTICIPANT', 'VISITOR');
INSERT INTO roles_feature (removed, roles_id, feature, "permission")
    SELECT NULL AS "removed", r.id AS roles_id, 'COMPETENCE' AS "feature", CASE WHEN r.role_type = 'ADMINISTRATOR' THEN 4 ELSE 2 END AS "permission"
    FROM roles r
    WHERE r.role_type IN ('ADMINISTRATOR', 'PARTICIPANT', 'VISITOR');

UPDATE roles_profile rp
    SET roles_id = (
        SELECT r.id
        FROM roles r
        WHERE r.group_id = rp.group_id
            AND r.role_type = CASE
                WHEN rp.default_role = 1 THEN 'ADMINISTRATOR'
                ELSE 'PARTICIPANT'
            END
    )
    WHERE roles_id IS NULL;

ALTER TABLE roles_profile
    DROP COLUMN default_role,
    ALTER COLUMN roles_id SET NOT NULL;

ALTER TABLE roles
    ALTER COLUMN role_type SET NOT NULL;
