CREATE TABLE institution (
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),
    name            VARCHAR(255),
    description     TEXT,
    created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_institution PRIMARY KEY (id)
);

COMMENT ON TABLE institution IS 'Tabela para armazenar informações sobre Instituições';

COMMENT ON COLUMN institution.id IS 'ID único da instituição';
COMMENT ON COLUMN institution.name IS 'Nome da instituição';
COMMENT ON COLUMN institution.description IS 'Descrição da instituição';
COMMENT ON COLUMN institution.created_at IS 'Data e hora de criação do registro';
