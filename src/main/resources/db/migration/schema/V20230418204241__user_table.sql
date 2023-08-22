
CREATE TABLE system_users
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    username            VARCHAR(255) NOT NULL UNIQUE,
    email               VARCHAR(255),
    password            VARCHAR(255),
    email_verified      BOOLEAN  NOT NULL,
    expired_user        BOOLEAN  NOT NULL,
    blocked_account     BOOLEAN  NOT NULL,
    expired_credentials BOOLEAN  NOT NULL,
    inactive            BOOLEAN  NOT NULL,
    authority           VARCHAR(255),

    CONSTRAINT pk_system_user PRIMARY KEY (id)
);

comment on table system_users is 'Tabela que contém os usuários do sistema.';