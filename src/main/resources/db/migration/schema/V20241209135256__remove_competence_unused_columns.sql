-- remove unused columns
ALTER TABLE competence
    DROP COLUMN title,
    DROP COLUMN start_date,
    DROP COLUMN end_date,
    DROP COLUMN present_date;
