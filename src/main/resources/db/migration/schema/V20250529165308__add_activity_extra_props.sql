ALTER TABLE activity.activity
    ADD COLUMN location TEXT NOT NULL,
    ADD COLUMN workload INTEGER NOT NULL,
    ADD COLUMN start_date DATE NOT NULL,
    ADD COLUMN end_date DATE NOT NULL;
