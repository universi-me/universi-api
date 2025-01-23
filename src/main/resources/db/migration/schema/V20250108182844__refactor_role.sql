ALTER TABLE roles
    RENAME TO role;

CREATE SCHEMA system_group;

ALTER TABLE role
    SET SCHEMA system_group;
