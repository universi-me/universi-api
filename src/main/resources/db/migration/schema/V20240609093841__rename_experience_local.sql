INSERT INTO experience_local (id, name, created_at, deleted)
    SELECT id, name, created_at, deleted
    FROM institution;

ALTER TABLE education
    DROP CONSTRAINT fk_education_institution;

DROP TABLE institution;

ALTER TABLE experience_local
    RENAME TO institution;

ALTER TABLE experience
    RENAME COLUMN local TO institution_id;

ALTER TABLE education
    ADD CONSTRAINT fk_education_institution FOREIGN KEY (institution_id) references institution (id);
