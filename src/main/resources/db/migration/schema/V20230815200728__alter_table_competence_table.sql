ALTER TABLE competence
    ADD COLUMN "title" VARCHAR(255);

ALTER TABLE competence
	ADD COLUMN "start_date" TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE competence
	ADD COLUMN "end_date" TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE competence
	ADD COLUMN "present_date" Boolean;

COMMENT ON COLUMN competence.id IS 'Id de cada competência';
COMMENT ON COLUMN competence.competence_type_id IS 'Tipo da competência';
COMMENT ON COLUMN competence.profile_id  IS 'A quem pertence está competência';
COMMENT ON COLUMN competence."title" IS 'Titulo relacionado a competência';
COMMENT ON COLUMN competence."description" IS 'Descrição da competência';
COMMENT ON COLUMN competence."level"  IS 'Id do nível da competência, por exemplo, 1 é pouco experiente...';
COMMENT ON COLUMN competence."created_at" IS 'Data da criação da competência';
COMMENT ON COLUMN competence."start_date" IS 'Data de inicio da competência, seja ela um curso, experiência etc. Dado opcional';
COMMENT ON COLUMN competence."end_date" IS 'Data de inicio da competência, seja ela um curso, experiência etc. Dado opcional';
COMMENT ON COLUMN competence."present_date" IS 'Se a competência ainda está sendo execuutad';
