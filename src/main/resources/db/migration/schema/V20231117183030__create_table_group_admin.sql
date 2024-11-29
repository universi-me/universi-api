
CREATE TABLE group_admin
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,

    profile_id          UUID NOT NULL,
    group_id            UUID NOT NULL,

    added               TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed             TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_group_admin PRIMARY KEY (id)
);

