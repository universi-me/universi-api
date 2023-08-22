
CREATE TABLE subject
(
    id      UUID NOT NULL DEFAULT uuid_generate_v4(),
    subject VARCHAR(255),

    CONSTRAINT pk_subject PRIMARY KEY (id)
);