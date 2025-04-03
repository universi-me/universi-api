-- create schema
CREATE SCHEMA competence;

-- update competence
ALTER TABLE competence
    SET SCHEMA competence;

-- update competence_type
ALTER TABLE competence_type
    SET SCHEMA competence;

-- update competence_type_profiles_with_access
ALTER TABLE competence_type_profiles_with_access
    SET SCHEMA competence;
