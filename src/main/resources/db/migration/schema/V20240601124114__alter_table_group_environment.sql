
ALTER TABLE group_environment
    ALTER COLUMN "message_template_new_content"         TYPE VARCHAR(6000),
    ALTER COLUMN "message_template_assigned_content"    TYPE VARCHAR(6000);
