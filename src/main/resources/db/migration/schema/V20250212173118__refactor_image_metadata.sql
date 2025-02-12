
ALTER TABLE image.image_metadata
    ADD COLUMN "public" BOOLEAN NOT NULL DEFAULT FALSE;
