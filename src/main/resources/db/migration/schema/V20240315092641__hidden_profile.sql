ALTER TABLE profile
    ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE public.profile
    SET hidden = TRUE
    WHERE user_id IN (
        SELECT id
        FROM system_users u
        WHERE u.username IN (
            'admin',
            'dev',
            'user'
        )
    );
