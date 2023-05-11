CREATE SEQUENCE group_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE profile_group
(
    id_group   INT8 NOT NULL,
    id_profile INT8 NOT NULL
);

CREATE TABLE subgroup
(
    id_group    INT8 NOT NULL,
    id_subgroup INT8 NOT NULL
);

CREATE TABLE system_group
(
    id_group            INT8 NOT NULL DEFAULT nextval('group_sequence'),
    nickname            VARCHAR(255),
    name                VARCHAR(255),
    description         TEXT,
    image               VARCHAR(255),
    id_profile          INT8,
    type                VARCHAR(255),
    group_root          BOOLEAN                                 NOT NULL,
    can_create_group    BOOLEAN                                 NOT NULL,
    can_enter           BOOLEAN                                 NOT NULL,
    can_add_participant BOOLEAN                                 NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    public_group        BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_system_group PRIMARY KEY (id_group)
);

ALTER TABLE system_group
    ADD CONSTRAINT FK_SYSTEM_GROUP_ON_ID_PROFILE FOREIGN KEY (id_profile) REFERENCES profile (id_profile);

ALTER TABLE profile_group
    ADD CONSTRAINT fk_progro_on_group FOREIGN KEY (id_group) REFERENCES system_group (id_group);

ALTER TABLE profile_group
    ADD CONSTRAINT fk_progro_on_profile FOREIGN KEY (id_profile) REFERENCES profile (id_profile);

ALTER TABLE subgroup
    ADD CONSTRAINT fk_subgroup_on_id_group FOREIGN KEY (id_group) REFERENCES system_group (id_group);

ALTER TABLE subgroup
    ADD CONSTRAINT fk_subgroup_on_id_subgroup FOREIGN KEY (id_subgroup) REFERENCES system_group (id_group);

