
CREATE TABLE videoplaylist_videos
(
	videos_id UUID NOT NULL,
	playlists_id UUID NOT NULL
);

ALTER TABLE videoplaylist_videos ADD CONSTRAINT fk_videoplaylist_videos_on_id_group FOREIGN KEY (playlists_id) REFERENCES videoplaylist(id);
ALTER TABLE videoplaylist_videos ADD CONSTRAINT fk_videoplaylist_videos_on_id_subgroup FOREIGN KEY (videos_id) REFERENCES video(id);