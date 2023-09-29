CREATE TABLE type_experience (
    id UUID UNIQUE PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE type_experience IS 'Tabela para armazenar tipos de experiência';

COMMENT ON COLUMN type_experience.id IS 'ID único do tipo de experiência';
COMMENT ON COLUMN type_experience.name IS 'Nome do tipo de experiência';
COMMENT ON COLUMN type_experience.created_at IS 'Data e hora de criação do registro';
