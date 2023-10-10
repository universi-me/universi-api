
CREATE TABLE image (
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),
    filename        VARCHAR(255),
    content_type    VARCHAR(255),
    size            BIGINT,
    profile_id      UUID,
    data            BYTEA,
    created         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_image PRIMARY KEY (id)
);
