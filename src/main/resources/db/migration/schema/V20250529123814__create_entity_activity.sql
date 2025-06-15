CREATE SCHEMA activity;

CREATE TABLE activity.activity (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    author_id UUID NOT NULL REFERENCES profile.profile ( id ),
    deleted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);

CREATE TABLE activity.participant (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    activity_id UUID NOT NULL REFERENCES activity.activity ( id ),
    profile_id UUID NOT NULL REFERENCES profile.profile ( id ),
    joined_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    removed_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);

CREATE TABLE activity.badges (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    activity_id UUID NOT NULL REFERENCES activity.activity ( id ),
    competence_type_id UUID NOT NULL REFERENCES competence.competence_type ( id )
);
