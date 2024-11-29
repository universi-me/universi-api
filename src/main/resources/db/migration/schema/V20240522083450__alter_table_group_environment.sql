
ALTER TABLE group_environment
	ADD COLUMN "email_enabled"      BOOLEAN NOT NULL DEFAULT FALSE,
	ADD COLUMN "email_host"         VARCHAR(255),
    ADD COLUMN "email_port"         VARCHAR(10),
    ADD COLUMN "email_protocol"     VARCHAR(15),
    ADD COLUMN "email_username"     VARCHAR(255),
    ADD COLUMN "email_password"     VARCHAR(255);


