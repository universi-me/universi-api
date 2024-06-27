-- Create a temporary table to hold unique institution data
CREATE TEMPORARY TABLE temp_institution AS
SELECT DISTINCT ON (name) *
FROM institution
ORDER BY name, id;

-- Insert unique institution data into experience_local
INSERT INTO experience_local (id, name, created_at, deleted)
SELECT id, name, created_at, deleted
FROM temp_institution;

ALTER TABLE education
    DROP CONSTRAINT fk_education_institution;

DROP TABLE institution;

ALTER TABLE experience_local
    RENAME TO institution;

ALTER TABLE experience
    RENAME COLUMN local TO institution_id;

ALTER TABLE education
    ADD CONSTRAINT fk_education_institution FOREIGN KEY (institution_id) references institution (id);
