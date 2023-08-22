
CREATE TABLE competence
(
    id                 UUID NOT NULL DEFAULT uuid_generate_v4(),
    competence_type_id UUID NOT NULL,
    profile_id         UUID NOT NULL,
    description        TEXT,
    level              VARCHAR(255),
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_competence PRIMARY KEY (id),
    CONSTRAINT FK_COMPETENCE_ON_ID_COMPETENCE_TYPE FOREIGN KEY (competence_type_id) REFERENCES competence_type(id),
    CONSTRAINT FK_COMPETENCE_ON_ID_PROFILE FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE
);
