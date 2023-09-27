CREATE TABLE education (
    id                      UUID NOT NULL DEFAULT uuid_generate_v4(),
    profile_id              UUID NOT NULL,
    type_education_id       UUID NOT NULL,
    institution_id          UUID NOT NULL,
    start_date              DATE NOT NULL,
    end_date                DATE,
    present_date            BOOLEAN,
    created_at              TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_education PRIMARY KEY (id)
);

-- Adicione uma chave estrangeira para a tabela Profile
ALTER TABLE education
ADD CONSTRAINT fk_education_profile FOREIGN KEY (profile_id) REFERENCES profile(id);

-- Adicione uma chave estrangeira para a tabela TypeEducation
ALTER TABLE education
ADD CONSTRAINT fk_education_type_education FOREIGN KEY (type_education_id) REFERENCES type_education(id);

-- Adicione uma chave estrangeira para a tabela Institution
ALTER TABLE education
ADD CONSTRAINT fk_education_institution FOREIGN KEY (institution_id) REFERENCES institution(id);


COMMENT ON TABLE education IS 'Tabela para armazenar informações sobre a formação';
COMMENT ON COLUMN education.id IS 'ID único da formação';
COMMENT ON COLUMN education.profile_id IS 'ID do perfil relacionado';
COMMENT ON COLUMN education.type_education_id IS 'ID do tipo de formação relacionado';
COMMENT ON COLUMN education.institution_id IS 'ID da instituição relacionada';
COMMENT ON COLUMN education.start_date IS 'Data de início';
COMMENT ON COLUMN education.end_date IS 'Data de término';
COMMENT ON COLUMN education.present_date IS 'Verifica se o usuário ainda não terminou';
COMMENT ON COLUMN education.created_at IS 'Data e hora de criação do registro';



