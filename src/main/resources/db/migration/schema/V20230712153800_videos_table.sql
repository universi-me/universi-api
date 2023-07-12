CREATE SEQUENCE  IF NOT EXISTS video_sequence
    INCREMENT BY 1
    START WITH 1
    MAXVALUE 9223372036854775807
    MINVALUE 1
    NO CYCLE;


CREATE TABLE videos
(
    id int8 NOT NULL DEFAULT nextval('video_sequence'),
    category varchar(255),
    description varchar(200),
    rating integer NOT NULL,
    title varchar(100),
    url varchar(45),
    playlist varchar(255),
    CONSTRAINT videos_pkey PRIMARY KEY (id),
    CONSTRAINT uk_mmxjhkqv59708plh51q5grbfb UNIQUE (title),
    CONSTRAINT uk_rakfu444jbybm7co7v46an5th UNIQUE (url),
    CONSTRAINT videos_rating_check CHECK (rating <= 5 AND rating >= 0)
)