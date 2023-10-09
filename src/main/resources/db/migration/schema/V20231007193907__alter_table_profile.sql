-- Remover a coluna profile_id da tabela competence
ALTER TABLE competence DROP COLUMN profile_id;

-- Cria a tabela de junção competence_profile
CREATE TABLE competence_profile (
    profile_id UUID NOT NULL DEFAULT uuid_generate_v4(),
    competence_id UUID NOT NULL DEFAULT uuid_generate_v4(),

    CONSTRAINT pk_competence_profile PRIMARY KEY (profile_id, competence_id),
    CONSTRAINT fk_profile_id FOREIGN KEY (profile_id) REFERENCES profile(id),
    CONSTRAINT fk_competence_id FOREIGN KEY (competence_id) REFERENCES competence(id)
);
