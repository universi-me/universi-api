
ALTER TABLE system_users
    ALTER COLUMN "authority" TYPE VARCHAR(15);

ALTER TABLE system_users
    ALTER COLUMN "password" TYPE VARCHAR(70);