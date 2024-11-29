
-- Insert unique rows from institution to experience_local where name does not already exist
INSERT INTO experience_local (id, name, created_at, deleted)
SELECT id, name, created_at, deleted
FROM institution
WHERE NOT EXISTS (
    SELECT 1
    FROM experience_local
    WHERE experience_local.name = institution.name
);

ALTER TABLE education
    DROP CONSTRAINT fk_education_institution;

UPDATE education
    SET institution_id = (
        SELECT id
        FROM experience_local el
        WHERE el.name = (
            SELECT name
            FROM institution i
            WHERE i.name = el.name AND i.id = institution_id
        )
    );

DROP TABLE institution;

ALTER TABLE experience_local
    RENAME TO institution;

ALTER TABLE experience
    RENAME COLUMN local TO institution_id;

ALTER TABLE education
    ADD CONSTRAINT fk_education_institution FOREIGN KEY (institution_id) references institution (id);
