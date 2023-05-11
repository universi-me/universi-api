CREATE SEQUENCE profile_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE profile
(
    id_profile    INT8 NOT NULL DEFAULT nextval('profile_sequence'),
    user_user_id  INT8,
    name          VARCHAR(255),
    lastname      VARCHAR(255),
    image         VARCHAR(255),
    bio           TEXT,
    id_link       INT8,
    gender        VARCHAR(255),
    creation_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_profile PRIMARY KEY (id_profile)
);

ALTER TABLE profile
    ADD CONSTRAINT FK_PROFILE_ON_USER FOREIGN KEY (user_user_id) REFERENCES system_user (user_id);
