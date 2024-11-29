
ALTER TABLE video RENAME TO content;
ALTER TABLE videoplaylist RENAME TO folder;
ALTER TABLE videocategory RENAME TO category;

ALTER TABLE video_categories RENAME TO content_categories;
ALTER TABLE content_categories RENAME COLUMN video_id TO content_id;

ALTER TABLE videoplaylist_categories RENAME TO folder_categories;
ALTER TABLE folder_categories RENAME COLUMN videoplaylist_id TO folder_id;

ALTER TABLE videoplaylist_videos RENAME TO folder_contents;
ALTER TABLE folder_contents RENAME COLUMN playlists_id TO folders_id;
ALTER TABLE folder_contents RENAME COLUMN videos_id TO contents_id;




