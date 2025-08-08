UPDATE system_users
    SET deleted = true
    WHERE username = 'dev' OR username = 'user';
