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

ALTER TABLE experience
    DROP COLUMN local,
    ALTER COLUMN local_tmp SET NOT NULL;

ALTER TABLE experience
    RENAME COLUMN local_tmp TO local;
