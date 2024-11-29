
CREATE TABLE group_environment
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    group_settings_id   UUID NOT NULL,

    signup_enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    signup_confirm_account_enabled BOOLEAN NOT NULL DEFAULT FALSE,

    login_google_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    google_client_id    VARCHAR(255),

    recaptcha_enabled   BOOLEAN NOT NULL DEFAULT FALSE,
    recaptcha_api_key  VARCHAR(255),
    recaptcha_api_project_id VARCHAR(255),
    recaptcha_site_key VARCHAR(255),

    added               TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed             TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_group_environment PRIMARY KEY (id)
);

