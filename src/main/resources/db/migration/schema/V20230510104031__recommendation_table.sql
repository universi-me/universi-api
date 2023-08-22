
CREATE TABLE recommendation
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    profile_origin_id   UUID NOT NULL,
    profile_destiny_id  UUID NOT NULL,
    competence_type_id  UUID NOT NULL,
    description         TEXT,
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_recommendation PRIMARY KEY (id),
    CONSTRAINT FK_RECOMMENDATION_ON_DESTINY FOREIGN KEY (profile_destiny_id) REFERENCES profile(id),
    CONSTRAINT FK_RECOMMENDATION_ON_ID_COMPETENCE_TYPE FOREIGN KEY (competence_type_id) REFERENCES competence_type(id),
    CONSTRAINT FK_RECOMMENDATION_ON_ORIGIN FOREIGN KEY (profile_origin_id) REFERENCES profile(id)
);
