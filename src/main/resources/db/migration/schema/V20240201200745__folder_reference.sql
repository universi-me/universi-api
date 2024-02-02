ALTER TABLE folder
    ADD COLUMN "reference" VARCHAR(15);

UPDATE folder
    SET "reference" = SUBSTR(MD5(RANDOM()::text), 0, 15)
    WHERE "reference" IS NULL;

ALTER TABLE folder
    ALTER COLUMN "reference" SET NOT NULL,
    ADD CONSTRAINT folder_reference_key UNIQUE ("reference");
