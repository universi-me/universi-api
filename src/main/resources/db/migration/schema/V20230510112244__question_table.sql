CREATE SEQUENCE question_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE question
(
    id int8 NOT NULL DEFAULT nextval('question_sequence'),
    title varchar(512) NOT NULL,
    feedback_id int8 not null,
    user_create_id int8 not null ,

    CONSTRAINT question_pkey PRIMARY KEY (id),
    CONSTRAINT feedback_id_pkey FOREIGN KEY (feedback_id) references feedback(id) ON DELETE CASCADE,
    CONSTRAINT user_create_id_pkey foreign key (user_create_id) references system_user(user_id)
);

CREATE TABLE exercise_question
(
    exercise_id INT8 NOT NULL,
    question_id INT8 NOT NULL
);

ALTER TABLE exercise_question
    ADD CONSTRAINT fk_exercise_question_on_question FOREIGN KEY (question_id) REFERENCES question (id);

ALTER TABLE exercise_question
    ADD CONSTRAINT fk_exercise_question_on_exercise FOREIGN KEY (exercise_id) REFERENCES exercise (id);