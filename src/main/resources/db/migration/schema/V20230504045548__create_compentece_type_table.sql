
CREATE TABLE competence_type
(
    id      UUID NOT NULL DEFAULT uuid_generate_v4(),
    name    VARCHAR(255),

    CONSTRAINT pk_competence_type PRIMARY KEY (id),
    CONSTRAINT uc_competence_type_name UNIQUE (name)
);
