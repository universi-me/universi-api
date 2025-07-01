ALTER TABLE system_group.group_environment
    ADD COLUMN "google_login_text" VARCHAR(255),
    ADD COLUMN "google_login_image_url" VARCHAR(255),
    ADD COLUMN "keycloak_login_text" VARCHAR(255),
    ADD COLUMN "keycloak_login_image_url" VARCHAR(255);
