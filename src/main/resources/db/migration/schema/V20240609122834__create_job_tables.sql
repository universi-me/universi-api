CREATE TABLE job (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    short_description TEXT NOT NULL,
    long_description TEXT NOT NULL,
    institution_id UUID REFERENCES institution (id) NOT NULL,
    author_id UUID REFERENCES profile (id) NOT NULL,
    closed BOOLEAN NOT NULL DEFAULT FALSE,

    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE job_competences (
    job_id UUID REFERENCES job (id),
    competence_type_id UUID REFERENCES competence_type (id)
);
