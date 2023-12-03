
ALTER TABLE group_features
    DROP COLUMN "contents";

ALTER TABLE group_features
    DROP COLUMN "groups";

ALTER TABLE group_features
    DROP COLUMN "participants";

ALTER TABLE group_features
    ADD COLUMN "name" VARCHAR(30) NOT NULL;

ALTER TABLE group_features
    ADD COLUMN "description" VARCHAR(130);

ALTER TABLE group_features
    ADD COLUMN "enabled" BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE group_features
    ADD COLUMN added TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE group_features
    ADD COLUMN removed TIMESTAMP WITHOUT TIME ZONE;
