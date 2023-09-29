CREATE TABLE type_education (
    id UUID UNIQUE PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE type_education IS 'Tabela para armazenar informações sobre o tipo da formação';

COMMENT ON COLUMN type_education.id IS 'ID único do tipo da formação';
COMMENT ON COLUMN type_education.name IS 'Nome do tipo da formação';
COMMENT ON COLUMN type_education.created_at IS 'Data e hora de criação';