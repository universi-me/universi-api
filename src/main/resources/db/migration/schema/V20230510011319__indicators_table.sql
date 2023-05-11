CREATE SEQUENCE indicators_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

create table indicators (
                            id int8 NOT NULL DEFAULT nextval('indicators_sequence'),
                            score int8 NOT NULL DEFAULT 0,
                            user_id int8 NOT NULL,

                            CONSTRAINT user_id_pk FOREIGN KEY (user_id) REFERENCES system_user(user_id),
                            CONSTRAINT indicators_pkey PRIMARY KEY (id)

);

comment on table indicators is 'Nesta tabela estão presentes as pontuações dos usuários';