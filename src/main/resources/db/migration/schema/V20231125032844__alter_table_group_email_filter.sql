
ALTER TABLE group_email_filter
    DROP COLUMN "regex";

ALTER TABLE group_email_filter
    ADD COLUMN "type" VARCHAR(15) NOT NULL DEFAULT 'END_WITH';
