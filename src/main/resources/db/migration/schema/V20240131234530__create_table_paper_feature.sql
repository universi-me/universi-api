
CREATE TABLE paper
(
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),

    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed         TIMESTAMP WITHOUT TIME ZONE,

    name            VARCHAR(30) NOT NULL,
    description     VARCHAR(130),
    group_id        UUID NOT NULL,

    CONSTRAINT pk_paper PRIMARY KEY (id)
);

CREATE TABLE paper_profile
(
    id              UUID NOT NULL DEFAULT uuid_generate_v4(),

    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    created         TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed         TIMESTAMP WITHOUT TIME ZONE,

    paper_id        UUID NOT NULL,
    profile_id      UUID NOT NULL,
    group_id        UUID NOT NULL,

    CONSTRAINT pk_paper_profile PRIMARY KEY (id),
    CONSTRAINT fk_paper_profile_paper FOREIGN KEY (paper_id) REFERENCES paper (id),
    CONSTRAINT fk_paper_profile_profile FOREIGN KEY (profile_id) REFERENCES profile (id),
    CONSTRAINT fk_paper_profile_group FOREIGN KEY (group_id) REFERENCES system_group (id)
);

CREATE TABLE paper_feature
(
    id                         UUID NOT NULL DEFAULT uuid_generate_v4(),

    deleted                    BOOLEAN NOT NULL DEFAULT FALSE,
    created                    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    removed                    TIMESTAMP WITHOUT TIME ZONE,

    paper_id                   UUID NOT NULL,
    feature                    VARCHAR(30) NOT NULL,

    permission                 INT NOT NULL DEFAULT 0,

    CONSTRAINT pk_paper_feature PRIMARY KEY (id),
    CONSTRAINT fk_paper_feature_paper FOREIGN KEY (paper_id) REFERENCES paper (id)
);
