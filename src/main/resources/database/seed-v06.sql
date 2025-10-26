PRAGMA foreign_keys = ON;

-- Ensure the 3 required languages exist
INSERT OR IGNORE INTO language_catalog(name) VALUES ('Java'),('Python'),('C++');

-- ===== 5 students =====
INSERT OR IGNORE INTO student(name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
VALUES
('Alice Chen',   'Senior',    1, 'Intern @ Acme',             'Front-End', 1, 0),
('Brian Lee',    'Junior',    0, NULL,                        'Back-End',  0, 0),
('Cam Nguyen',   'Graduate',  1, 'TA - CS Dept.',             'Data',      1, 0),
('Diego Torres', 'Sophomore', 0, NULL,                        'Full-Stack',0, 0),
('Eva Park',     'Senior',    1, 'Open-source contributor',   'Other',     0, 0);

-- =====================================================
-- Skills: PROGRAMMING LANGUAGES
-- =====================================================

-- Alice: Java, Python, C++
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Alice Chen';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Alice Chen';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'C++'    FROM student WHERE name='Alice Chen';

-- Brian: C++, Java
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'C++'    FROM student WHERE name='Brian Lee';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Brian Lee';

-- Cam: Python, Java
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Cam Nguyen';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Cam Nguyen';

-- Diego: C++, Python
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'C++'    FROM student WHERE name='Diego Torres';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Diego Torres';

-- Eva: Python, Java
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Eva Park';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Eva Park';

-- ====================================
-- Skills: DATABASES
-- ====================================

-- Alice: MySQL, SQLite
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MySQL'  FROM student WHERE name='Alice Chen';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'SQLite' FROM student WHERE name='Alice Chen';

-- Brian: Postgres, MySQL
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'Postgres' FROM student WHERE name='Brian Lee';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MySQL'    FROM student WHERE name='Brian Lee';

-- Cam: MongoDB, Postgres
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MongoDB'  FROM student WHERE name='Cam Nguyen';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'Postgres' FROM student WHERE name='Cam Nguyen';

-- Diego: MySQL, SQL Server
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MySQL'     FROM student WHERE name='Diego Torres';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'SQL Server'FROM student WHERE name='Diego Torres';

-- Eva: SQLite, MongoDB
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'SQLite'  FROM student WHERE name='Eva Park';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MongoDB' FROM student WHERE name='Eva Park';

-- ===============================
-- Comments (dedup per student/body)
-- ===============================

-- Helper pattern used below:
-- INSERT INTO comments(student_id, body, created_at)
-- SELECT s.id, 'text', '2025-10-10 09:00'
-- FROM student s
-- WHERE s.name='Full Name'
--   AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='text');

-- Alice
INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Great front-end instincts and attention to detail.', '2025-10-10 09:00'
FROM student s
WHERE s.name='Alice Chen'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Great front-end instincts and attention to detail.');

INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Delivered a solid prototype demo this week.', '2025-10-14 14:30'
FROM student s
WHERE s.name='Alice Chen'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Delivered a solid prototype demo this week.');

-- Brian
INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Prefers backend tasks.', '2025-10-08 11:20'
FROM student s
WHERE s.name='Brian Lee'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Prefers backend tasks.');

INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Studying database indexing strategies.', '2025-10-16 16:05'
FROM student s
WHERE s.name='Brian Lee'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Studying database indexing strategies.');

-- Cam
INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Great progress on data course.', '2025-10-09 10:45'
FROM student s
WHERE s.name='Cam Nguyen'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Great progress on data course.');

INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Presented a clean ETL mini-project.', '2025-10-17 13:10'
FROM student s
WHERE s.name='Cam Nguyen'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Presented a clean ETL mini-project.');

-- Diego
INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Enjoys full-stack; needs more test coverage.', '2025-10-07 15:00'
FROM student s
WHERE s.name='Diego Torres'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Enjoys full-stack; needs more test coverage.');

INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Code review went well—good PR descriptions.', '2025-10-15 17:35'
FROM student s
WHERE s.name='Diego Torres'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Code review went well—good PR descriptions.');

-- Eva
INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Strong communicator; helpful in lab sessions.', '2025-10-11 08:55'
FROM student s
WHERE s.name='Eva Park'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Strong communicator; helpful in lab sessions.');

INSERT INTO comments(student_id, body, created_at)
SELECT s.id, 'Interested in DevOps; exploring CI/CD basics.', '2025-10-18 12:20'
FROM student s
WHERE s.name='Eva Park'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Interested in DevOps; exploring CI/CD basics.');