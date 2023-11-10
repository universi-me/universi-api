ALTER TABLE system_users
	ADD COLUMN organization UUID REFERENCES system_group (id);

UPDATE system_users
	SET organization = (
		SELECT g.id
		FROM system_group g
		WHERE g.group_root
		ORDER BY g.created_at ASC
		LIMIT 1
	)
	WHERE organization IS NULL;

ALTER TABLE system_users
	DROP CONSTRAINT system_users_username_key;

ALTER TABLE system_users
	ADD CONSTRAINT system_users_username_organization_key UNIQUE (username, organization);
