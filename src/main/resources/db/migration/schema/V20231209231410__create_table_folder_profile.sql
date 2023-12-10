
DROP TABLE IF EXISTS users_folders;

CREATE TABLE folder_profile
(
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,

    author_id       UUID NOT NULL,
    profile_id      UUID NOT NULL,
    folder_id       UUID NOT NULL,

    assigned        BOOLEAN NOT NULL DEFAULT FALSE,

    created         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed         TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_folder_profile PRIMARY KEY (id)
);

