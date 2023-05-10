alter table system_user
    add constraint indicators_id_fk foreign key (indicators_id) references indicators(id) on delete cascade;

comment on table system_user is 'Tabela que contém os usuários do sistema.';