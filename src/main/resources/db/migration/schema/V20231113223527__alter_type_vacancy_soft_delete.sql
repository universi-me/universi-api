
ALTER TABLE type_vacancy
    RENAME COLUMN is_deleted TO deleted;

ALTER TABLE type_vacancy
    ALTER COLUMN deleted TYPE BOOLEAN;

ALTER TABLE type_vacancy
    ALTER COLUMN deleted SET DEFAULT FALSE;