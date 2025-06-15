-- create schema
CREATE SCHEMA capacity;

-- update category
ALTER TABLE category
    SET SCHEMA capacity;

-- update content
ALTER TABLE content
    SET SCHEMA capacity;

-- update content_status
ALTER TABLE contentstatus
    RENAME TO content_status;
ALTER TABLE content_status
    SET SCHEMA capacity;

-- update folder
ALTER TABLE folder
    SET SCHEMA capacity;

ALTER TABLE folder_granted_access_groups
    SET SCHEMA capacity;

ALTER TABLE folder_competences
    SET SCHEMA capacity;

-- update folder_contents
ALTER TABLE folder_contents
    SET SCHEMA capacity;

-- update folder_favorite
ALTER TABLE folder_favorite
    SET SCHEMA capacity;

-- update folder_profile
UPDATE folder_profile
SET removed = CURRENT_TIMESTAMP
WHERE deleted IS TRUE
    AND removed IS NULL;

-- update folder_profile
ALTER TABLE folder_profile
    DROP COLUMN deleted;

ALTER TABLE folder_profile
    SET SCHEMA capacity;

-- update folder_categories
ALTER TABLE folder_categories
    SET SCHEMA capacity;

-- update content_categories
ALTER TABLE content_categories
    SET SCHEMA capacity;
