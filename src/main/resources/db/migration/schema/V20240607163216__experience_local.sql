CREATE TABLE experience_local (
    id         UUID NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
    name       TEXT UNIQUE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO experience_local (name)
    SELECT e.local
    FROM experience e
    WHERE NOT e.deleted
    GROUP BY e.local;

ALTER TABLE experience
    ADD COLUMN local_tmp UUID REFERENCES experience_local (id);

UPDATE experience
    SET local_tmp = (
        SELECT el.id
        FROM experience_local el
        WHERE local = el.name
        LIMIT 1
    );

-- Ensure no rows have NULL local_tmp values
-- Create a temporary table to hold rows with NULL local_tmp
CREATE TEMP TABLE experience_tmp AS
SELECT * FROM experience WHERE local_tmp IS NULL;

-- Now delete rows with NULL local_tmp from experience_profile to avoid foreign key constraint issues
DELETE FROM experience_profile
WHERE experience_id IN (SELECT id FROM experience_tmp);

-- Delete rows with NULL local_tmp from experience table
DELETE FROM experience
    WHERE local_tmp IS NULL;

ALTER TABLE experience
    DROP COLUMN local,
    ALTER COLUMN local_tmp SET NOT NULL;

ALTER TABLE experience
    RENAME COLUMN local_tmp TO local;
