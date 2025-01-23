CREATE SCHEMA profile;
ALTER TABLE public.profile SET SCHEMA profile;
ALTER TABLE public.profile_competence_badges SET SCHEMA profile;

CREATE SCHEMA link;
ALTER TABLE public.link SET SCHEMA link;

CREATE SCHEMA institution;
ALTER TABLE public.institution SET SCHEMA institution;

CREATE SCHEMA job;
ALTER TABLE public.job SET SCHEMA job;
ALTER TABLE public.job_competences SET SCHEMA job;

ALTER TABLE system_group SET SCHEMA system_group;
ALTER TABLE group_email_filter SET SCHEMA system_group;
ALTER TABLE group_environment SET SCHEMA system_group;
ALTER TABLE group_features SET SCHEMA system_group;
ALTER TABLE group_settings SET SCHEMA system_group;
ALTER TABLE group_theme SET SCHEMA system_group;
ALTER TABLE profile_group SET SCHEMA system_group;
ALTER TABLE subgroup SET SCHEMA system_group;
