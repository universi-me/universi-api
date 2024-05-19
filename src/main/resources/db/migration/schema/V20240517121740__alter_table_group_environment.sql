
ALTER TABLE group_environment
	ADD COLUMN "message_new_content_enabled" BOOLEAN NOT NULL DEFAULT TRUE,
	ADD COLUMN "message_template_new_content" VARCHAR(255),
	ADD COLUMN "message_assigned_content_enabled" BOOLEAN NOT NULL DEFAULT TRUE,
	ADD COLUMN "message_template_assigned_content" VARCHAR(255);


