CREATE TABLE system_group.type (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    label TEXT NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);

INSERT INTO system_group.type ( label )
SELECT DISTINCT "type" FROM system_group.system_group;

UPDATE system_group.type t
SET label = CASE t.label
    WHEN 'INSTITUTION' THEN 'Instituição'
    WHEN 'CAMPUS' THEN 'Campus'
    WHEN 'COURSE' THEN 'Curso'
    WHEN 'PROJECT' THEN 'Projeto'
    WHEN 'CLASSROOM' THEN 'Sala de Aula'
    WHEN 'MONITORIA' THEN 'Monitoria'
    WHEN 'LABORATORY' THEN 'Laboratório'
    WHEN 'ACADEMIC_CENTER' THEN 'Centro Acadêmico'
    WHEN 'DEPARTMENT' THEN 'Departamento'
    WHEN 'STUDY_GROUP' THEN 'Grupo de Estudos'
    WHEN 'GROUP_GENERAL' THEN 'Grupo Geral'
    WHEN 'DIRECTORATE' THEN 'Diretoria'
    WHEN 'MANAGEMENT' THEN 'Gerência'
    WHEN 'COORDINATION' THEN 'Coordenação'
    WHEN 'COMPANY_AREA' THEN 'Área da Empresa'
    WHEN 'DEVELOPMENT_TEAM' THEN 'Time de Desenvolvimento'
    WHEN 'INTEREST_GROUP' THEN 'Grupo de Interesse'
    WHEN 'MISCELLANEOUS_SUBJECTS' THEN 'Assuntos Diversos'
    WHEN 'ENTERTAINMENT' THEN 'Entretenimento'
    ELSE t.label
END;

ALTER TABLE system_group.system_group
    ADD COLUMN type_id UUID REFERENCES system_group.type ( id );

UPDATE system_group.system_group g
SET type_id = (
    SELECT t.id
    FROM system_group.type t
    WHERE g.type = t.label
);

ALTER TABLE system_group.system_group
    ALTER COLUMN type_id SET NOT NULL;

ALTER TABLE system_group.system_group
    DROP COLUMN type;
