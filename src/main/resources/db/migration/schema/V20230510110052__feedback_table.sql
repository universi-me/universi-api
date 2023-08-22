
CREATE TABLE feedback
(
    id            UUID         NOT NULL DEFAULT uuid_generate_v4(),
    link          VARCHAR(256) NOT NULL,
    feedback_text VARCHAR(512) NOT NULL,

    CONSTRAINT feedback_pkey PRIMARY KEY (id)
);