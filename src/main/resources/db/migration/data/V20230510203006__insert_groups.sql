INSERT INTO system_group
(id_group, can_add_participant, can_create_group, can_enter, created_at, description, image, "name", nickname, public_group, group_root, "type", id_profile)
VALUES(default, false, false, false, '2023-05-04 20:48:25.635', 'UFPB Campus IV', NULL, 'CENTRO DE CIÊNCIAS APLICADAS A EDUCAÇÃO', 'CCAE', true, true, ' CAMPUS', 1);

INSERT INTO profile_group
(id_profile, id_group)
VALUES(1, 1);
--------------------
INSERT INTO system_group
(id_group, can_add_participant, can_create_group, can_enter, created_at, description, image, "name", nickname, public_group, group_root, "type", id_profile)
VALUES(default, false, false, false, '2023-05-04 20:48:25.635', 'DEPARTAMENTOS', NULL, 'DEPARTAMENTO DE CIÊNCIAS EXATAS', 'DCX', true, FALSE, 'DEPARTMENT', 1);

INSERT INTO profile_group
(id_profile, id_group)
VALUES(1, 2);

INSERT INTO public.subgroup
(id_group, id_subgroup)
VALUES(1, 2);

