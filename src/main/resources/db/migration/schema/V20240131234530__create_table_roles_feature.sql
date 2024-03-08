
CREATE TABLE roles
(
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),

    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed         TIMESTAMP WITHOUT TIME ZONE,

    name            VARCHAR(30) NOT NULL,
    description     VARCHAR(130),
    group_id        UUID NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE roles_profile
(
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),

    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed         TIMESTAMP WITHOUT TIME ZONE,

    roles_id        UUID,
    profile_id      UUID NOT NULL,
    group_id        UUID NOT NULL,

    default_role    INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT pk_roles_profile PRIMARY KEY (id),
    CONSTRAINT fk_roles_profile_roles FOREIGN KEY (roles_id) REFERENCES roles (id),
    CONSTRAINT fk_roles_profile_profile FOREIGN KEY (profile_id) REFERENCES profile (id),
    CONSTRAINT fk_roles_profile_group FOREIGN KEY (group_id) REFERENCES system_group (id)
);

CREATE TABLE roles_feature
(
    id                         UUID NOT NULL DEFAULT uuid_generate_v4(),

    deleted                    BOOLEAN NOT NULL DEFAULT FALSE,
    created                    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed                    TIMESTAMP WITHOUT TIME ZONE,

    roles_id                   UUID NOT NULL,
    feature                    VARCHAR(30) NOT NULL,

    permission                 INT NOT NULL DEFAULT 0,

    CONSTRAINT pk_roles_feature PRIMARY KEY (id),
    CONSTRAINT fk_roles_feature_roles FOREIGN KEY (roles_id) REFERENCES roles (id)
);
