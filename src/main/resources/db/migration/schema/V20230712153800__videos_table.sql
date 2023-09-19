
CREATE TABLE video
(
    id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
    url                 VARCHAR(45),
    title               VARCHAR(100),
    description         VARCHAR(200),
    category            VARCHAR(255),
    playlist            VARCHAR(255),
    rating              INTEGER NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_videos PRIMARY KEY (id),
    CONSTRAINT uc_videos_title UNIQUE (title),
    CONSTRAINT uc_videos_url UNIQUE (url),
    CONSTRAINT videos_rating_check CHECK (rating <= 5 AND rating >= 0)
);
