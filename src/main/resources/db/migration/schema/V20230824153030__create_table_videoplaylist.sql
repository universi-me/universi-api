
CREATE TABLE videoplaylist
(
	id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
	name                VARCHAR(100),
	image               VARCHAR(100),
	description         VARCHAR(200),
	rating              INTEGER NOT NULL,
	category_id		    UUID,
	profile_id		    UUID,
	created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

	CONSTRAINT pk_videoplaylist PRIMARY KEY (id),
	CONSTRAINT videoplaylist_rating_check CHECK (rating <= 5 AND rating >= 0)
);
