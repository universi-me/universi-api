INSERT INTO system_group
(can_add_participant, can_create_group, can_enter, description, image, "name", nickname, public_group, group_root, "type", profile_id)
VALUES(
    false, false, false, 'UFPB Campus IV', 'https://i.imgur.com/zmLl9zq.jpg', 'CENTRO DE CIÊNCIAS APLICADAS A EDUCAÇÃO', 'ccae', true, true, ' CAMPUS',
    (SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='admin'))
);

INSERT INTO profile_group
(profile_id, group_id)
VALUES(
    (SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='admin')),
    (SELECT id FROM system_group WHERE system_group.nickname='ccae')
);

--------------------
INSERT INTO system_group
(can_add_participant, can_create_group, can_enter, description, image, "name", nickname, public_group, group_root, "type", profile_id)
VALUES(
    false, false, false, 'DEPARTAMENTOS', 'https://i.imgur.com/I62K6IJ.png', 'DEPARTAMENTO DE CIÊNCIAS EXATAS', 'dcx', true, false, 'DEPARTMENT',
    (SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='admin'))
);

INSERT INTO profile_group(profile_id, group_id)
VALUES(
    (SELECT id FROM profile WHERE profile.user_id=(SELECT id FROM system_users WHERE system_users.username='admin')),
    (SELECT id FROM system_group WHERE system_group.nickname='dcx')
);


INSERT INTO subgroup
(group_id, subgroup_id)
VALUES(
    (SELECT id FROM system_group WHERE system_group.nickname='ccae'),
    (SELECT id FROM system_group WHERE system_group.nickname='dcx')
);

