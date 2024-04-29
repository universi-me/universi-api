ALTER TABLE folder_granted_access_groups
	ALTER COLUMN "folder_id" SET NOT NULL,
	ALTER COLUMN "granted_access_groups_id" SET NOT NULL;

ALTER TABLE folder_granted_access_groups
	ADD CONSTRAINT "folder_granted_access_groups_folder_id_granted_access_groups_id_key" UNIQUE ("folder_id", "granted_access_groups_id");

ALTER TABLE folder_granted_access_groups
	ADD CONSTRAINT fk_folder_granted_access_groups_on_id_folder FOREIGN KEY (folder_id) REFERENCES folder(id),
	ADD CONSTRAINT fk_folder_granted_access_groups_on_id_system_group FOREIGN KEY (granted_access_groups_id) REFERENCES system_group(id);

INSERT INTO folder_granted_access_groups
	SELECT id AS "folder_id", owner_group_id AS "granted_access_groups_id"
	FROM folder;

ALTER TABLE folder
	DROP COLUMN owner_group_id;
