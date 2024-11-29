CREATE TABLE type_vacancy (
    id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT pk_type_vacancy PRIMARY KEY (id)
);

COMMENT ON TABLE type_vacancy IS 'Tabela armazena informações sobre o tipo de vaga';

COMMENT ON COLUMN type_vacancy.id IS 'ID único do tipo de vaga';
COMMENT ON COLUMN type_vacancy.name IS 'Nome do tipo de vaga';
COMMENT ON COLUMN type_vacancy.created_at IS 'Data e hora de criação';
COMMENT ON COLUMN type_vacancy.is_deleted IS 'Indica se o registro foi excluído';