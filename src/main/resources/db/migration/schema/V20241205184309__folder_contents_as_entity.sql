-- Add id and deleted columns
ALTER TABLE folder_contents
    ADD COLUMN id UUID DEFAULT uuid_generate_v4(),
    ADD COLUMN deleted BOOLEAN DEFAULT FALSE;

UPDATE folder_contents
    SET id = uuid_generate_v4()
    WHERE id IS NULL;

UPDATE folder_contents
    SET deleted = FALSE
    WHERE deleted IS NULL;

ALTER TABLE folder_contents
    ALTER COLUMN id SET NOT NULL,
    ADD PRIMARY KEY (id);
