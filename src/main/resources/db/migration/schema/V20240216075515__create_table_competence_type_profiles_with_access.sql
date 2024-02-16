CREATE TABLE IF NOT EXISTS competence_type_profiles_with_access (
    competence_type_id UUID REFERENCES competence_type (id),
    profile_id UUID REFERENCES profile (id)
);
