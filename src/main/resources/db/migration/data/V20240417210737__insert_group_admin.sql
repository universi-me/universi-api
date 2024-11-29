-- Insert the group.admin profile to the admin list of group, check for prevent duplicate entry
CREATE OR REPLACE FUNCTION add_admin_of_group_to_list_if_missing()
RETURNS VOID AS $$
DECLARE
    group_id_now UUID;
BEGIN
FOR group_id_now IN (SELECT id FROM system_group) LOOP
    INSERT INTO group_admin(profile_id, group_id)
    SELECT
    (SELECT system_group.profile_id FROM system_group WHERE system_group.id=group_id_now),
    group_id_now
    WHERE NOT EXISTS (
        SELECT 1 FROM group_admin WHERE group_admin.profile_id=(SELECT system_group.profile_id FROM system_group WHERE system_group.id=group_id_now) AND group_admin.group_id=group_id_now
    );
END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT add_admin_of_group_to_list_if_missing();