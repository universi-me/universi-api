
CREATE TABLE profile_group
(
    group_id   UUID NOT NULL,
    profile_id UUID NOT NULL
);


CREATE TABLE subgroup
(
    group_id    UUID NOT NULL,
    subgroup_id UUID NOT NULL
);

comment on table subgroup is 'Nesta tabela estão presentes as relacoes de grupos com subgrupos';

CREATE TABLE system_group
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    nickname            VARCHAR(255),
    name                VARCHAR(255),
    description         TEXT,
    image               VARCHAR(255),
    profile_id          UUID NOT NULL,
    type                VARCHAR(255),
    group_root          BOOLEAN NOT NULL,
    can_create_group    BOOLEAN NOT NULL,
    can_enter           BOOLEAN NOT NULL,
    can_add_participant BOOLEAN NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    public_group        BOOLEAN NOT NULL,

    CONSTRAINT pk_system_group PRIMARY KEY (id)
);

comment on table system_group is 'Nesta tabela estão presentes os grupos do sistema.';

ALTER TABLE system_group ADD CONSTRAINT FK_SYSTEM_GROUP_ON_ID_PROFILE FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE profile_group ADD CONSTRAINT fk_progro_on_group FOREIGN KEY (group_id) REFERENCES system_group(id);
ALTER TABLE profile_group ADD CONSTRAINT fk_progro_on_profile FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE subgroup ADD CONSTRAINT fk_subgroup_on_id_group FOREIGN KEY (group_id) REFERENCES system_group(id);
ALTER TABLE subgroup ADD CONSTRAINT fk_subgroup_on_id_subgroup FOREIGN KEY (subgroup_id) REFERENCES system_group(id);