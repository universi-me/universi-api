-- insert user authority 'admin'
INSERT INTO system_users
("username", email, "password", email_verified, expired_user, blocked_account, expired_credentials, inactive, authority)
VALUES('admin', NULL, '$2a$10$j6KEd6tkoJINCXQChTmuye8/kvOP0MGSePQGLWmZbpFdVYARbDljW', false, false, false, false, false, 'ROLE_ADMIN');

INSERT INTO profile
(user_id, "name", lastname, image, bio, gender)
VALUES(
       (SELECT id FROM system_users WHERE system_users.username='admin'),
       'Admin',
       'User',
       'https://i.imgur.com/41p6RAG.png',
       NULL, NULL
);

INSERT INTO indicators(score, profile_id)
VALUES(0, (SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='admin')));

UPDATE profile SET indicators_id=(SELECT id FROM indicators
        WHERE indicators.profile_id=(SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='admin')))
WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='admin');

-- insert user authority 'dev'
INSERT INTO system_users
("username", email, "password", email_verified, expired_user, blocked_account, expired_credentials, inactive, authority)
VALUES('dev', NULL, '$2a$10$j6KEd6tkoJINCXQChTmuye8/kvOP0MGSePQGLWmZbpFdVYARbDljW', false, false, false, false, false, 'ROLE_DEV');

INSERT INTO profile
(user_id, "name", lastname, image, bio, gender)
VALUES(
          (SELECT id FROM system_users WHERE system_users.username='dev'),
          'Dev',
          'User',
          'https://i.imgur.com/41p6RAG.png',
          NULL, NULL
      );

INSERT INTO indicators(score, profile_id)
VALUES(0, (SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='dev')));

UPDATE profile SET indicators_id=(SELECT id FROM indicators
        WHERE indicators.profile_id=(SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='dev')))
WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='dev');

-- insert user authority 'user'
INSERT INTO system_users
("username", email, "password", email_verified, expired_user, blocked_account, expired_credentials, inactive, authority)
VALUES('user', NULL, '$2a$10$j6KEd6tkoJINCXQChTmuye8/kvOP0MGSePQGLWmZbpFdVYARbDljW', false, false, false, false, false, 'ROLE_USER');

INSERT INTO profile
(user_id, "name", lastname, image, bio, gender)
VALUES(
             (SELECT id FROM system_users WHERE system_users.username='user'),
             'User',
             'User',
             'https://i.imgur.com/41p6RAG.png',
             NULL, NULL
      );

INSERT INTO indicators(score, profile_id)
VALUES(0, (SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='user')));

UPDATE profile SET indicators_id=(SELECT id FROM indicators
        WHERE indicators.profile_id=(SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='user')))
WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='user');