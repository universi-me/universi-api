
ALTER TABLE system_group
    ADD COLUMN group_settings_id UUID;

CREATE TABLE group_settings
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_group_settings PRIMARY KEY (id)
);

CREATE TABLE group_email_filter
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    enabled             BOOLEAN NOT NULL DEFAULT FALSE,
    regex               BOOLEAN NOT NULL DEFAULT FALSE,
    email               VARCHAR(255),
    group_settings_id   UUID NOT NULL,
    added               TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed             TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_group_email_filter PRIMARY KEY (id)
);


CREATE OR REPLACE FUNCTION create_group_settings_for_system_group_if_missing()
RETURNS VOID AS $$
DECLARE
group_id UUID;
    sg_id UUID;
BEGIN
FOR group_id IN (SELECT id FROM system_group WHERE group_settings_id IS NULL) LOOP
    INSERT INTO group_settings DEFAULT VALUES RETURNING id INTO sg_id;
    UPDATE system_group SET group_settings_id = sg_id WHERE id = group_id;
END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT create_group_settings_for_system_group_if_missing();


ALTER TABLE system_group
    ALTER COLUMN group_settings_id SET NOT NULL;
