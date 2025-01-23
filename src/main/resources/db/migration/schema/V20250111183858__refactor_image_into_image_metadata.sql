CREATE SCHEMA image;

-- transform image into image_metadata
ALTER TABLE public.image RENAME TO image_metadata;
ALTER TABLE public.image_metadata SET SCHEMA image;

ALTER TABLE image.image_metadata
    DROP COLUMN "size",
    ADD COLUMN "stored_at" VARCHAR( 10 );

UPDATE image.image_metadata SET stored_at = 'DATABASE';

ALTER TABLE image.image_metadata RENAME COLUMN created TO created_at;

-- create image_data
CREATE TABLE image.image_data (
    metadata_id UUID NOT NULL PRIMARY KEY REFERENCES image.image_metadata ( id ),
    data BYTEA NOT NULL
);

INSERT INTO image.image_data ( metadata_id, data )
SELECT id as metadata_id, data FROM image.image_metadata;

ALTER TABLE image.image_metadata DROP COLUMN data;

-- transform image url into some image_metadata fields
CREATE FUNCTION get_image_info( image_url TEXT ) RETURNS TABLE( id UUID, store_type TEXT, _filename TEXT) AS $$
BEGIN
    IF image_url LIKE '/img/imagem/%.jpg' THEN
        RETURN QUERY SELECT UUID_GENERATE_V4(), 'FILESYSTEM', SUBSTRING( image_url FROM 13 FOR ( length( image_url ) - 16 ) );

    ELSIF image_url LIKE '/img/store/%' THEN
        RETURN QUERY SELECT UUID_GENERATE_V4(), 'DATABASE', SUBSTRING( image_url FROM 12 );

    ELSIF image_url LIKE '/img/minio/%' THEN
        RETURN QUERY SELECT UUID_GENERATE_V4(), 'MINIO', SUBSTRING( image_url FROM 12 );

    ELSE
        RETURN QUERY SELECT UUID_GENERATE_V4(), 'EXTERNAL', image_url;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- update a table from image url to metadata
CREATE PROCEDURE update_table( table_name TEXT, old_column TEXT, new_column TEXT ) AS $$
DECLARE
    query TEXT;

BEGIN
    query := 'ALTER TABLE ' || table_name || ' ADD COLUMN ' || new_column || ' UUID REFERENCES image.image_metadata';
    EXECUTE query;

    query := 'DO $' || '$
    DECLARE
        entity RECORD;
        image_info RECORD;

    BEGIN
    FOR entity IN SELECT * FROM ' || table_name || ' LOOP
        IF entity.' || old_column || ' IS NULL THEN CONTINUE; END IF;

        image_info := get_image_info( entity.' || old_column || ' );

        INSERT INTO image.image_metadata ( id, filename, content_type, profile_id, created_at, deleted, stored_at )
        VALUES ( image_info.id, image_info._filename, ''image/jpg'', entity.id, NOW(), FALSE, image_info.store_type );

        UPDATE ' || table_name || '
        SET ' || new_column || ' = ( SELECT id FROM image.image_metadata im WHERE im.id = image_info.id )
        WHERE id = entity.id;
    END LOOP;
    END;$' || '$';
    EXECUTE query;

    query := 'ALTER TABLE ' || table_name || ' DROP COLUMN ' || old_column || ';';
    EXECUTE query;
END;
$$ LANGUAGE plpgsql;

CALL update_table( 'public.profile',      'image',        'image_metadata_id' );
CALL update_table( 'capacity.content',    'image',        'image_metadata_id' );
CALL update_table( 'capacity.folder',     'image',        'image_metadata_id' );
CALL update_table( 'capacity.category',   'image',        'image_metadata_id' );
CALL update_table( 'public.system_group', 'image',        'image_metadata_id' );
CALL update_table( 'public.system_group', 'banner_image', 'banner_image_metadata_id' );
CALL update_table( 'public.system_group', 'header_image', 'header_image_metadata_id' );

DROP FUNCTION get_image_info;
DROP PROCEDURE update_table;
