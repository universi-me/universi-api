
-- Check if competence and competence_profile exist
DO $$
DECLARE
competence_exists BOOLEAN;
    competence_profile_exists BOOLEAN;
    education_exists BOOLEAN;
    education_profile_exists BOOLEAN;
    experience_exists BOOLEAN;
    experience_profile_exists BOOLEAN;
BEGIN
    -- Check if competence and competence_profile exist
SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE lower(table_name) = 'competence') INTO competence_exists;
SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE lower(table_name) = 'competence_profile') INTO competence_profile_exists;

IF competence_exists AND competence_profile_exists THEN
DELETE FROM competence_profile WHERE competence_id IN (SELECT id FROM competence WHERE deleted = true);
DELETE FROM competence WHERE deleted = true;
END IF;

    -- Check if education and education_profile exist
SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE lower(table_name) = 'education') INTO education_exists;
SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE lower(table_name) = 'education_profile') INTO education_profile_exists;

IF education_exists AND education_profile_exists THEN
DELETE FROM education_profile WHERE education_id IN (SELECT id FROM education WHERE deleted = true);
DELETE FROM education WHERE deleted = true;
END IF;

    -- Check if experience and experience_profile exist
SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE lower(table_name) = 'experience') INTO experience_exists;
SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE lower(table_name) = 'experience_profile') INTO experience_profile_exists;

IF experience_exists AND experience_profile_exists THEN
DELETE FROM experience_profile WHERE experience_id IN (SELECT id FROM experience WHERE deleted = true);
DELETE FROM experience WHERE deleted = true;
END IF;
END $$;