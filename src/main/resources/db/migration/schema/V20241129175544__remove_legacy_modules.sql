DROP TABLE indicators_achievement;
DROP TABLE achievement;

DROP TABLE alternative;

ALTER TABLE question
	DROP COLUMN feedback_id;
DROP TABLE feedback;

DROP TABLE competence_vacancy;
DROP TABLE vacancy;
DROP TABLE type_vacancy;

DROP TABLE "subject";

ALTER TABLE profile
	DROP COLUMN indicators_id;
DROP TABLE indicators;

DROP TABLE exercise_question;
DROP TABLE exercise;
DROP TABLE question;

DROP TABLE recommendation;
