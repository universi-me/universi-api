
CREATE TABLE videocategory
(
	id                  UUID NOT NULL DEFAULT uuid_generate_v4(),
	name                VARCHAR(100),
	image               VARCHAR(100),
	profile_id		    UUID,
	created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

	CONSTRAINT pk_videocategory PRIMARY KEY (id)
);
