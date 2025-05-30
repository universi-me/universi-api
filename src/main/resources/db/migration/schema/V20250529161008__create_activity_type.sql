CREATE TABLE activity.type (
    id UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);

ALTER TABLE activity.activity
    ADD COLUMN type_id UUID NOT NULL REFERENCES activity.type ( id );
