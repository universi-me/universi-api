DROP TABLE IF EXISTS folder_favorite;

CREATE TABLE folder_favorite (
    id      UUID    NOT NULL DEFAULT uuid_generate_v4(),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    profile_id UUID NOT NULL REFERENCES profile,
    folder_id  UUID NOT NULL REFERENCES folder,

    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_folder_favorite PRIMARY KEY (id),

    FOREIGN KEY (profile_id) REFERENCES profile(id),
    FOREIGN KEY (folder_id)  REFERENCES folder(id)
);
