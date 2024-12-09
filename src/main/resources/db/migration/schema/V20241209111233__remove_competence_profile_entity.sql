-- add profile column
ALTER TABLE competence
    ADD COLUMN profile_id UUID;

-- fetch profile_id from CompetenceProfile entity
UPDATE competence u_c
SET profile_id = (
    SELECT cp.profile_id
    FROM competence_profile cp
        INNER JOIN competence c ON cp.competence_id = c.id
    WHERE cp.competence_id = u_c.id
)
WHERE profile_id IS NULL;

-- add reference to Profile
ALTER TABLE competence
    ADD CONSTRAINT fk_competence_profile FOREIGN KEY ( profile_id ) references profile (id),
    ALTER COLUMN profile_id SET NOT NULL;

-- drop CompetenceProfile
DROP TABLE competence_profile;
