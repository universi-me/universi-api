
ALTER TABLE system_users
    ALTER COLUMN "username" TYPE VARCHAR(50);

ALTER TABLE system_users
    ALTER COLUMN "email" TYPE VARCHAR(90);

ALTER TABLE system_users
    ALTER COLUMN "recovery_token" TYPE VARCHAR(64);

ALTER TABLE profile
    ALTER COLUMN "name" TYPE VARCHAR(50);

ALTER TABLE profile
    ALTER COLUMN "lastname" TYPE VARCHAR(50);

ALTER TABLE profile
    ALTER COLUMN "gender" TYPE VARCHAR(4);

ALTER TABLE system_group
    ALTER COLUMN "nickname" TYPE VARCHAR(50);

ALTER TABLE system_group
    ALTER COLUMN "name" TYPE VARCHAR(50);

ALTER TABLE system_group
    ALTER COLUMN "type" TYPE VARCHAR(30);

