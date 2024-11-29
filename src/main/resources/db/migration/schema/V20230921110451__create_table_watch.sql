
CREATE TABLE watch
(
    id            UUID NOT NULL DEFAULT uuid_generate_v4(),
    status        VARCHAR(50) NOT NULL,
    content_id    UUID NOT NULL,
    profile_id    UUID NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_watch PRIMARY KEY (id),
    CONSTRAINT Fk_watch_on_id_content FOREIGN KEY (content_id) REFERENCES content(id)
);