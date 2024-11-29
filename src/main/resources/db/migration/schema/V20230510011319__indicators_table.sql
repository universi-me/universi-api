
create table indicators (
    id             UUID NOT NULL DEFAULT uuid_generate_v4(),
    score          int8 NOT NULL DEFAULT 0,
    profile_id     UUID,

    CONSTRAINT indicators_pkey PRIMARY KEY (id)
);

ALTER TABLE indicators
    ADD CONSTRAINT profile_id_pk FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE profile
    ADD CONSTRAINT FK_PROFILE_ON_INDICATORS FOREIGN KEY (indicators_id) REFERENCES indicators(id);

comment on table indicators is 'Nesta tabela estão presentes as pontuações dos profiles';