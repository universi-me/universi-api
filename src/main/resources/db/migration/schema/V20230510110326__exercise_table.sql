
CREATE TABLE exercise
(
    id         UUID NOT NULL DEFAULT uuid_generate_v4(),
    title      VARCHAR(255) NOT NULL,
    group_id   UUID NOT NULL,
    inactivate boolean not null default false,

    CONSTRAINT pk_exercise PRIMARY KEY (id),
    CONSTRAINT FK_EXERCISE_ON_GROUP FOREIGN KEY (group_id) REFERENCES system_group(id)
);

comment on column exercise.inactivate
    is 'Inactive exercise';