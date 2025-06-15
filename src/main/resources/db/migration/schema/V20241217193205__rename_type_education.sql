ALTER TABLE type_education
    RENAME TO education_type;

CREATE SCHEMA education;

ALTER TABLE education_type
    SET SCHEMA education;
