
CREATE TABLE profile
(
    id            UUID NOT NULL DEFAULT uuid_generate_v4(),
    user_id       UUID,
    name          VARCHAR(255),
    lastname      VARCHAR(255),
    image         VARCHAR(255),
    bio           TEXT,
    gender        VARCHAR(255),
    indicators_id UUID,
    created_at    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_profile PRIMARY KEY (id)
);

ALTER TABLE profile
    ADD CONSTRAINT FK_PROFILE_ON_USER FOREIGN KEY (user_id) REFERENCES system_users(id);