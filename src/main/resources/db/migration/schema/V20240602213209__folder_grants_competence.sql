CREATE TABLE folder_competences (
    folder_id          UUID NOT NULL REFERENCES folder (id),
    competence_type_id UUID NOT NULL REFERENCES competence_type (id)
);

CREATE TABLE profile_competence_badges (
    profile_id         UUID NOT NULL REFERENCES profile (id),
    competence_type_id UUID NOT NULL REFERENCES competence_type (id)
);
