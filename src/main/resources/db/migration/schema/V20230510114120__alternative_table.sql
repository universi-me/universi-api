
CREATE TABLE alternative
(
    id             UUID         NOT NULL DEFAULT uuid_generate_v4(),
    title          varchar(512) NOT NULL,
    correct        BOOL         DEFAULT FALSE,
    question_id    UUID         NOT NULL,

    CONSTRAINT alternative_pkey PRIMARY KEY (id),
    CONSTRAINT question_id_pkey FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE
);
