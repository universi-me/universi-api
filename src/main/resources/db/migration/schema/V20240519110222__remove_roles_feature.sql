ALTER TABLE roles
    ADD COLUMN feed_permission INT,
    ADD COLUMN content_permission INT,
    ADD COLUMN group_permission INT,
    ADD COLUMN people_permission INT,
    ADD COLUMN competence_permission INT;

UPDATE roles updt
    SET feed_permission = (
        SELECT permission
        FROM roles_feature search
        WHERE search.roles_id = updt.id
            AND feature = 'FEED'
    ),
    content_permission = (
        SELECT permission
        FROM roles_feature search
        WHERE search.roles_id = updt.id
            AND feature = 'CONTENT'
    ),
    group_permission = (
        SELECT permission
        FROM roles_feature search
        WHERE search.roles_id = updt.id
            AND feature = 'GROUP'
    ),
    people_permission = (
        SELECT permission
        FROM roles_feature search
        WHERE search.roles_id = updt.id
            AND feature = 'PEOPLE'
    ),
    competence_permission = (
        SELECT permission
        FROM roles_feature search
        WHERE search.roles_id = updt.id
            AND feature = 'COMPETENCE'
    );

DROP TABLE roles_feature;

ALTER TABLE roles
    ALTER COLUMN feed_permission SET NOT NULL,
    ALTER COLUMN content_permission SET NOT NULL,
    ALTER COLUMN group_permission SET NOT NULL,
    ALTER COLUMN people_permission SET NOT NULL,
    ALTER COLUMN competence_permission SET NOT NULL;
