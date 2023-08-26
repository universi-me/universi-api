
ALTER TABLE video
	DROP COLUMN "category_id";

ALTER TABLE videoplaylist
	DROP COLUMN "category_id";

CREATE TABLE video_categories
(
	video_id UUID NOT NULL,
	categories_id UUID NOT NULL
);

ALTER TABLE video_categories
    ADD CONSTRAINT fk_video_categories_on_video_id FOREIGN KEY (video_id) REFERENCES video(id);
ALTER TABLE video_categories
    ADD CONSTRAINT fk_video_categories_on_categories_id FOREIGN KEY (categories_id) REFERENCES videocategory(id);

CREATE TABLE videoplaylist_categories
(
	videoplaylist_id UUID NOT NULL,
	categories_id UUID NOT NULL
);

ALTER TABLE videoplaylist_categories
    ADD CONSTRAINT fk_videoplaylist_categories_on_videoplaylist_id FOREIGN KEY (videoplaylist_id) REFERENCES videoplaylist(id);
ALTER TABLE videoplaylist_categories
    ADD CONSTRAINT fk_videoplaylist_categories_on_categories_id FOREIGN KEY (categories_id) REFERENCES videocategory(id);
