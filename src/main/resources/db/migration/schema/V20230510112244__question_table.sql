
CREATE TABLE question
(
    id           UUID NOT NULL DEFAULT uuid_generate_v4(),
    title        varchar(512) NOT NULL,
    feedback_id  UUID NOT NULL,
    profile_id   UUID NOT NULL,

    CONSTRAINT question_pkey PRIMARY KEY (id),
    CONSTRAINT feedback_id_pkey FOREIGN KEY (feedback_id) references feedback(id) ON DELETE CASCADE,
    CONSTRAINT profile_id_pkey foreign key (profile_id) references profile(id)
);

CREATE TABLE exercise_question
(
    exercise_id UUID NOT NULL,
    question_id UUID NOT NULL,

    CONSTRAINT fk_exercise_question_on_question FOREIGN KEY (question_id) REFERENCES question(id),
    CONSTRAINT fk_exercise_question_on_exercise FOREIGN KEY (exercise_id) REFERENCES exercise(id)
);
