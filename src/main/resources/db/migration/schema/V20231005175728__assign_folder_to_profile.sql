CREATE TABLE users_folders (
    folder_id  UUID REFERENCES folder (id),
    profile_id UUID REFERENCES profile(id),

    UNIQUE(folder_id, profile_id)
);
