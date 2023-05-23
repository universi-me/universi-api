alter table exercise add column
    inactivate boolean not null default false;

comment on column exercise.inactivate
    is 'Inactive exercise';