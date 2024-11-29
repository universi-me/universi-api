
ALTER TABLE group_environment
	ADD COLUMN "keycloak_enabled" BOOLEAN NOT NULL DEFAULT FALSE,
	ADD COLUMN "keycloak_client_id" VARCHAR(255),
	ADD COLUMN "keycloak_client_secret" VARCHAR(255),
	ADD COLUMN "keycloak_realm" VARCHAR(255),
	ADD COLUMN "keycloak_url" VARCHAR(255),
	ADD COLUMN "keycloak_redirect_url" VARCHAR(255);


