ALTER TABLE competence ADD COLUMN level_numeric INT;

UPDATE competence
SET level_numeric =
    CASE
        WHEN level = 'NO_EXPERIENCE' THEN 0
        WHEN level = 'LITTLE_EXPERIENCE' THEN 1
        WHEN level = 'EXPERIENCED' THEN 2
        WHEN level IN ('VERY_EXPERIENCED','MASTER') THEN 3
        ELSE NULL
    END;

ALTER TABLE competence DROP COLUMN level;
-- ALTER TABLE competence RENAME COLUMN level to old_level;
ALTER TABLE competence RENAME COLUMN level_numeric TO level;
