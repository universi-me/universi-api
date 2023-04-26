CREATE SEQUENCE user_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE system_user
(
    user_id             int8 NOT NULL DEFAULT nextval('user_sequence'),
    name                VARCHAR(255),
    email               VARCHAR(255),
    password            VARCHAR(255),
    email_verified      BOOLEAN  NOT NULL,
    expired_user        BOOLEAN  NOT NULL,
    blocked_account     BOOLEAN  NOT NULL,
    expired_credentials BOOLEAN  NOT NULL,
    inactive            BOOLEAN  NOT NULL,
    authority           VARCHAR(255),
    indicators_id       int8,
    CONSTRAINT pk_system_user PRIMARY KEY (user_id)
);