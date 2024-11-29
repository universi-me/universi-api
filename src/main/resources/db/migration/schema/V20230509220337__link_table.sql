
CREATE TABLE link
(
    id         UUID NOT NULL DEFAULT uuid_generate_v4(),
    type_link  VARCHAR(255),
    url        VARCHAR(255),
    name       VARCHAR(255),
    profile_id UUID NOT NULL,

    CONSTRAINT pk_link PRIMARY KEY (id),
    CONSTRAINT FK_LINK_ON_ID_PROFILE FOREIGN KEY (profile_id) REFERENCES profile (id) ON DELETE CASCADE
);
