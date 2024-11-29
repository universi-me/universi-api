
-- Remove all deleted roles_profile
DELETE FROM roles_profile WHERE deleted = true;

-- Delete duplicated rows profile and group in roles_profile
WITH CTE AS (
    SELECT
        id,
        profile_id,
        group_id,
        ROW_NUMBER() OVER (PARTITION BY profile_id, group_id ORDER BY id) AS row_num
    FROM
        roles_profile
)
DELETE FROM roles_profile
WHERE id IN (
    SELECT id
    FROM CTE
    WHERE row_num > 1
);
