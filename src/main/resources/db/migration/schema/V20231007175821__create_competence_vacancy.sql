CREATE TABLE competence_vacancy (
    vacancy_id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    competence_id UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),

    CONSTRAINT pk_competence_vacancy PRIMARY KEY (vacancy_id, competence_id),
    CONSTRAINT fk_vacancy_id FOREIGN KEY (vacancy_id) REFERENCES vacancy(id),
    CONSTRAINT fk_competence_id FOREIGN KEY (competence_id) REFERENCES competence(id)
);