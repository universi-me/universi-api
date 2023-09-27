CREATE TABLE profile_experience (
    id                      UUID NOT NULL DEFAULT uuid_generate_v4(),
    profile_id              UUID NOT NULL,
    type_experience_id      UUID NOT NULL,
    local                   VARCHAR(255),
    description             TEXT,
    start_date              DATE NOT NULL,
    end_date                DATE,
    present_date            BOOLEAN,
    created_at              TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_profile_experience PRIMARY KEY (id)
);

ALTER TABLE profile_experience
ADD CONSTRAINT fk_profile_experience_profile FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE profile_experience
ADD CONSTRAINT fk_profile_experience_type_experience FOREIGN KEY (type_experience_id) REFERENCES type_experience(id);

COMMENT ON TABLE profile_experience IS 'Tabela para armazenar experiências';

COMMENT ON COLUMN profile_experience.id IS 'ID único da experiência';
COMMENT ON COLUMN profile_experience.profile_id IS 'ID do perfil relacionado';
COMMENT ON COLUMN profile_experience.type_experience_id IS 'ID do tipo de experiência relacionado';
COMMENT ON COLUMN profile_experience.local IS 'Local da experiência';
COMMENT ON COLUMN profile_experience.description IS 'Descrição da experiência';
COMMENT ON COLUMN profile_experience.start_date IS 'Data de início da experiência';
COMMENT ON COLUMN profile_experience.end_date IS 'Data de término da experiência';
COMMENT ON COLUMN profile_experience.present_date IS 'Indica se ainda atua na experiência indicada';
COMMENT ON COLUMN profile_experience.created_at IS 'Data e hora de criação do registro';
