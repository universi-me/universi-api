CREATE TABLE component(
    id                    UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
    component_type_id    UUID NOT NULL,
    profile_id             UUID NOT NULL,
    title                TEXT,
    description            TEXT,
    start_date            TIMESTAMP WITHOUT TIME ZONE,
    end_date            TIMESTAMP WITHOUT TIME ZONE,
    present_date        BOOLEAN,
    created_at            TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT component_pkey PRIMARY KEY (id),
    CONSTRAINT Fk_component_on_id_component_type FOREIGN KEY (component_type_id) REFERENCES component_type(id),
    CONSTRAINT Fk_component_on_id_profile FOREIGN KEY (profile_id) REFERENCES profile(id)



);