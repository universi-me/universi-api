CREATE SEQUENCE  IF NOT EXISTS video_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;

CREATE TABLE videos
(
    id int8 NOT NULL DEFAULT nextval('video_sequence'),
    url         VARCHAR(45),
    title       VARCHAR(100),
    description VARCHAR(200),
    category    VARCHAR(255),
    playlist    VARCHAR(255),
    rating      INTEGER NOT NULL,
    CONSTRAINT pk_videos PRIMARY KEY (id)
);

ALTER TABLE videos
    ADD CONSTRAINT uc_videos_title UNIQUE (title);

ALTER TABLE videos
    ADD CONSTRAINT uc_videos_url UNIQUE (url);

ALTER TABLE videos
    ADD CONSTRAINT videos_rating_check CHECK (rating <= 5 AND rating >= 0);