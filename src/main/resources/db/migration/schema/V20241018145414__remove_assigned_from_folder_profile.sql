ALTER TABLE folder_profile
	DROP COLUMN assigned;

ALTER TABLE folder_profile
	RENAME COLUMN author_id TO assigned_by_id;

ALTER TABLE folder_profile
	RENAME COLUMN profile_id TO assigned_to_id;
