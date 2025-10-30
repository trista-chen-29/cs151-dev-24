-- Languages (exactly 3; idempotent)
INSERT OR IGNORE INTO language_catalog(name) VALUES ('C++'), ('Java'), ('Python');

-- Ava Chen (only unemployed)
INSERT INTO student (name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
VALUES ('Ava Chen','Junior',0,'','Front-End',0,0);
INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES
(last_insert_rowid(),'Java');

-- Brian Lee
INSERT INTO student (name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
VALUES ('Brian Lee','Junior',1,'Intern @XYZ','Back-End',1,0);
INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES
(last_insert_rowid(),'C++'),
(last_insert_rowid(),'Java');
INSERT OR IGNORE INTO databases(student_id, database_name) VALUES
(last_insert_rowid(),'MySQL');

-- Cam Nguyen
INSERT INTO student (name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
VALUES ('Cam Nguyen','Graduate',1,'Intern @ABC','Data',1,0);
INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES
(last_insert_rowid(),'Java'),
(last_insert_rowid(),'Python');
INSERT OR IGNORE INTO databases(student_id, database_name) VALUES
(last_insert_rowid(),'PostgreSQL');

-- Diego Torres
INSERT INTO student (name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
VALUES ('Diego Torres','Sophomore',1,'Part-time @DEF','Full-Stack',0,0);
INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES
(last_insert_rowid(),'C++'),
(last_insert_rowid(),'Python');
INSERT OR IGNORE INTO databases(student_id, database_name) VALUES
(last_insert_rowid(),'MongoDB');

-- Eva Park (blacklisted sample)
INSERT INTO student (name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
VALUES ('Eva Park','Senior',1,'TA @SJSU','Other',0,1);
INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES
(last_insert_rowid(),'Java');
