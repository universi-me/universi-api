CREATE TABLE profile.department (
    id      UUID NOT NULL PRIMARY KEY,
    name    TEXT NOT NULL,
    acronym TEXT NOT NULL,

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITHOUT TIME ZONE
);

ALTER TABLE profile.profile
    ADD COLUMN department_id UUID REFERENCES profile.department ( id );
