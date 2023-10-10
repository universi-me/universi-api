ALTER TABLE vacancy
ADD COLUMN type_vacancy_id UUID NOT NULL DEFAULT uuid_generate_v4(),
ADD COLUMN title VARCHAR(255),
ADD COLUMN prerequisites TEXT,
ADD COLUMN registration_date DATE NOT NULL,
ADD COLUMN end_registration_date DATE NOT NULL,
ADD COLUMN is_active BOOLEAN,
ADD COLUMN is_deleted BOOLEAN;

ALTER TABLE vacancy
ADD CONSTRAINT FK_VACANCY_TYPE_VACANCY FOREIGN KEY (type_vacancy_id) REFERENCES type_vacancy(id);


COMMENT ON COLUMN vacancy.type_vacancy_id IS 'ID único do tipo de vaga';
COMMENT ON COLUMN vacancy.title IS 'Título da vaga';
COMMENT ON COLUMN vacancy.prerequisites IS 'Pré-requisitos para a vaga';
COMMENT ON COLUMN vacancy.registration_date IS 'Data de registro da vaga';
COMMENT ON COLUMN vacancy.end_registration_date IS 'Data de encerramento do registro';
COMMENT ON COLUMN vacancy.is_active IS 'Indica se a vaga está ativa';
COMMENT ON COLUMN vacancy.is_deleted IS 'Indica se o registro foi excluído';
