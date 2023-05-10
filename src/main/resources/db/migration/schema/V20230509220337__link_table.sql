CREATE SEQUENCE link_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE link
(
    id_link    INT8 NOT NULL DEFAULT nextval('link_sequence'),
    type_link  VARCHAR(255),
    url        VARCHAR(255),
    name       VARCHAR(255),
    id_profile INT8,
    CONSTRAINT pk_link PRIMARY KEY (id_link)
);

ALTER TABLE link
    ADD CONSTRAINT FK_LINK_ON_ID_PROFILE FOREIGN KEY (id_profile) REFERENCES profile (id_profile);

ALTER TABLE profile
    ADD CONSTRAINT FK_PROFILE_ON_ID_LINK FOREIGN KEY (id_link) REFERENCES link (id_link);

