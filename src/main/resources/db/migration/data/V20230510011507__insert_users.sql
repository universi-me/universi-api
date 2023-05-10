-- insert user admin
INSERT INTO system_user
(user_id, "name", email, "password", email_verified, expired_user, blocked_account, expired_credentials, inactive, authority, indicators_id)
VALUES(default, 'admin', NULL, '$2a$10$j6KEd6tkoJINCXQChTmuye8/kvOP0MGSePQGLWmZbpFdVYARbDljW', false, false, false, false, false, 'ROLE_ADMIN', NULL);

INSERT INTO profile
(id_profile, user_user_id, "name", lastname, image, bio, id_link, gender, creation_date)
VALUES(default, 1, NULL, NULL, NULL, NULL, NULL, NULL, '2023-05-10 00:38:32.550');

insert into indicators (id, score, user_id) values (default, 0, 1);

UPDATE system_user
SET  indicators_id=1
WHERE user_id=1;


--------------

--insert user
INSERT INTO system_user
(user_id, "name", email, "password", email_verified, expired_user, blocked_account, expired_credentials, inactive, authority, indicators_id)
VALUES(default, 'user', NULL, '$2a$10$j6KEd6tkoJINCXQChTmuye8/kvOP0MGSePQGLWmZbpFdVYARbDljW', false, false, false, false, false, 'ROLE_USER', NULL);

INSERT INTO profile
(id_profile, user_user_id, "name", lastname, image, bio, id_link, gender, creation_date)
VALUES(default, 2, NULL, NULL, NULL, NULL, NULL, NULL, '2023-05-10 00:38:32.550');

insert into indicators (id, score, user_id) values (default, 0, 2);

UPDATE system_user
SET  indicators_id=2
WHERE user_id=2;

--------------------
--insert dev
INSERT INTO system_user
(user_id, "name", email, "password", email_verified, expired_user, blocked_account, expired_credentials, inactive, authority, indicators_id)
VALUES(default, 'dev', NULL, '$2a$10$j6KEd6tkoJINCXQChTmuye8/kvOP0MGSePQGLWmZbpFdVYARbDljW', false, false, false, false, false, 'ROLE_DEV', NULL);

INSERT INTO profile
(id_profile, user_user_id, "name", lastname, image, bio, id_link, gender, creation_date)
VALUES(default, 3, NULL, NULL, NULL, NULL, NULL, NULL, '2023-05-10 00:38:32.550');

insert into indicators (id, score, user_id) values (default, 0, 3);

UPDATE system_user
SET  indicators_id=3
WHERE user_id=3;