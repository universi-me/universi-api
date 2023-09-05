CREATE TABLE component_type
(
    id                 UUID unique NOT NULL DEFAULT uuid_generate_v4(),
    name 			   TEXT NOT null unique,
    created_at         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_component_type PRIMARY KEY (id)
);