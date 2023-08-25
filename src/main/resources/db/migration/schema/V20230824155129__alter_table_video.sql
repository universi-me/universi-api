ALTER TABLE video
	DROP COLUMN category;

ALTER TABLE video
	DROP COLUMN playlist;

ALTER TABLE video
    ALTER COLUMN "url" TYPE VARCHAR(100);

ALTER TABLE video
	ADD COLUMN "category_id" UUID;

ALTER TABLE video
	ADD COLUMN "profile_id" UUID;

ALTER TABLE video
	ADD COLUMN "image" VARCHAR(100);

ALTER TABLE video DROP CONSTRAINT uc_videos_title;
ALTER TABLE video DROP CONSTRAINT uc_videos_url;

COMMENT ON COLUMN video.image IS 'thumbnail image url do video.';
COMMENT ON COLUMN video.category_id IS 'id da categoria do video.';
