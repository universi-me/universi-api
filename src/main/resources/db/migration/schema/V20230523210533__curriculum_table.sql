CREATE SEQUENCE curriculum_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE public.curriculum (
	id_curriculum int8 NOT NULL DEFAULT nextval('curriculum_sequence'),
	creation_date timestamp(6) NULL,
	description text NULL,
	id_profile int8 NULL,
	CONSTRAINT curriculum_pkey PRIMARY KEY (id_curriculum),
	CONSTRAINT uk_id_profile_curriculum UNIQUE (id_profile)
);


-- public.curriculum foreign keys

ALTER TABLE public.curriculum ADD CONSTRAINT pk_id_profile_curriculum FOREIGN KEY (id_profile) REFERENCES public.profile(id_profile);