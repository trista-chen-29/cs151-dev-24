PRAGMA foreign_keys = ON;

-- Ensure the 3 required languages exist in the catalog
INSERT OR IGNORE INTO language_catalog(name) VALUES ('Java'),('Python'),('C++');

-- ===== 6 students (3 whitelisted, 3 blacklisted) =====
INSERT OR IGNORE INTO student(
    name,
    academic_status,
    employed,
    job_details,
    preferred_role,
    whitelist,
    isBlacklisted
)
VALUES
    -- Whitelisted only
    ('Alice Chen',   'Senior',     1, 'Intern @ Acme',             'Front-End', 1, 0),
    ('Cam Nguyen',   'Graduate',   1, 'TA - CS Dept.',             'Data',      1, 0),
    ('Eva Park',     'Senior',     1, 'Open-source contributor',   'Other',     1, 0),

    -- Blacklisted only
    ('Brian Lee',    'Junior',     0, NULL,                        'Back-End',  0, 1),
    ('Diego Torres', 'Sophomore',  0, 'Full-stack project helper', 'Full-Stack',0, 1),
    ('Farah Ali',    'Junior',     1, 'Part-time QA assistant',    'QA',        0, 1);

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

-- Farah: Java, C++
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Farah Ali';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'C++'    FROM student WHERE name='Farah Ali';

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
SELECT id, 'MySQL'      FROM student WHERE name='Diego Torres';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'SQL Server' FROM student WHERE name='Diego Torres';

-- Eva: SQLite, MongoDB
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'SQLite'  FROM student WHERE name='Eva Park';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MongoDB' FROM student WHERE name='Eva Park';

-- Farah: MySQL, SQLite
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MySQL'  FROM student WHERE name='Farah Ali';
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'SQLite' FROM student WHERE name='Farah Ali';

-- ===============================
-- Comments (dedup per student/body)
-- ===============================

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
SELECT s.id, 'Prefers backend tasks and low-level optimization.', '2025-10-08 11:20'
FROM student s
WHERE s.name='Brian Lee'
  AND NOT EXISTS (SELECT 1 FROM comments c WHERE c.student_id=s.id AND c.body='Prefers backend tasks and low-level optimization.');

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
 PRAGMA foreign_keys = OFF;

-- 1️⃣ Delete old data to avoid duplicates
DELETE FROM comments;
DELETE FROM programming_languages;
DELETE FROM databases;
DELETE FROM student;

PRAGMA foreign_keys = ON;

-- 2️⃣ Insert 6 students (3 whitelist, 3 blacklist)

INSERT INTO student (name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted) VALUES
-- Whitelisted (3)
('Alice Chen', 'Senior', 1, 'Intern @ Acme', 'Front-End', 1, 0),
('Cam Nguyen', 'Graduate', 1, 'TA - CS Dept.', 'Data', 1, 0),
('Eva Park', 'Senior', 1, 'Open-source contributor', 'Other', 1, 0),

-- Blacklisted (3)
('Brian Lee', 'Junior', 0, NULL, 'Back-End', 0, 1),
('Diego Torres', 'Sophomore', 0, 'Full-stack project helper', 'Full-Stack', 0, 1),
('Farah Ali', 'Junior', 1, 'Part-time QA assistant', 'QA', 0, 1);

-- ===========================================
-- 3️⃣ Programming Languages
-- ===========================================

-- Alice: Java, Python, C++
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Java' FROM student WHERE name='Alice Chen';
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Alice Chen';
INSERT INTO programming_languages(student_id, language)
SELECT id, 'C++' FROM student WHERE name='Alice Chen';

-- Brian: C++, Java
INSERT INTO programming_languages(student_id, language)
SELECT id, 'C++' FROM student WHERE name='Brian Lee';
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Java' FROM student WHERE name='Brian Lee';

-- Cam: Python, Java
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Cam Nguyen';
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Java' FROM student WHERE name='Cam Nguyen';

-- Diego: C++, Python
INSERT INTO programming_languages(student_id, language)
SELECT id, 'C++' FROM student WHERE name='Diego Torres';
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Diego Torres';

-- Eva: Python, Java
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Eva Park';
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Java' FROM student WHERE name='Eva Park';

-- Farah: Java, C++
INSERT INTO programming_languages(student_id, language)
SELECT id, 'Java' FROM student WHERE name='Farah Ali';
INSERT INTO programming_languages(student_id, language)
SELECT id, 'C++' FROM student WHERE name='Farah Ali';

-- =======================================
-- 4️⃣ Databases
-- =======================================
-- Alice: MySQL, SQLite
INSERT INTO databases(student_id, database_name)
SELECT id, 'MySQL' FROM student WHERE name='Alice Chen';
INSERT INTO databases(student_id, database_name)
SELECT id, 'SQLite' FROM student WHERE name='Alice Chen';

-- Brian: Postgres, MySQL
INSERT INTO databases(student_id, database_name)
SELECT id, 'Postgres' FROM student WHERE name='Brian Lee';
INSERT INTO databases(student_id, database_name)
SELECT id, 'MySQL' FROM student WHERE name='Brian Lee';

-- Cam: MongoDB, Postgres
INSERT INTO databases(student_id, database_name)
SELECT id, 'MongoDB' FROM student WHERE name='Cam Nguyen';
INSERT INTO databases(student_id, database_name)
SELECT id, 'Postgres' FROM student WHERE name='Cam Nguyen';

-- Diego: MySQL, SQL Server
INSERT INTO databases(student_id, database_name)
SELECT id, 'MySQL' FROM student WHERE name='Diego Torres';
INSERT INTO databases(student_id, database_name)
SELECT id, 'SQL Server' FROM student WHERE name='Diego Torres';

-- Eva: SQLite, MongoDB
INSERT INTO databases(student_id, database_name)
SELECT id, 'SQLite' FROM student WHERE name='Eva Park';
INSERT INTO databases(student_id, database_name)
SELECT id, 'MongoDB' FROM student WHERE name='Eva Park';

-- Farah: MySQL, SQLite
INSERT INTO databases(student_id, database_name)
SELECT id, 'MySQL' FROM student WHERE name='Farah Ali';
INSERT INTO databases(student_id, database_name)
SELECT id, 'SQLite' FROM student WHERE name='Farah Ali';

-- =======================================
-- 5️⃣ Comments (minimum 30 words each)
-- =======================================

-- Alice
INSERT INTO comments(student_id, body, created_at)
SELECT id,
'She consistently produces clean, well-structured front-end code and communicates clearly with teammates. She takes initiative, responds constructively to feedback, and prepares detailed UI demos ahead of deadlines.',
'2025-10-10'
FROM student WHERE name='Alice Chen';

-- Brian
INSERT INTO comments(student_id, body, created_at)
SELECT id,
'He enjoys backend development and low-level optimization but occasionally needs reminders regarding deadlines. However, when focused, he demonstrates strong problem-solving skills and contributes valuable code improvements.',
'2025-10-12'
FROM student WHERE name='Brian Lee';

-- Cam
INSERT INTO comments(student_id, body, created_at)
SELECT id,
'He shows excellent understanding of data structures and database concepts, mentors peers during lab hours, and writes organized, efficient code. He also thoroughly tests his work before pushing to the repository.',
'2025-10-15'
FROM student WHERE name='Cam Nguyen';

-- Diego
INSERT INTO comments(student_id, body, created_at)
SELECT id,
'He shows interest in full-stack development and quickly learns new frameworks. He occasionally rushes changes without testing, but he is very receptive to feedback and steadily improving his code quality.',
'2025-10-18'
FROM student WHERE name='Diego Torres';

-- Eva
INSERT INTO comments(student_id, body, created_at)
SELECT id,
'She is very engaged, asks thoughtful questions, documents group discussions clearly, and completes tasks without needing reminders. She is exploring DevOps and demonstrates strong communication and teamwork skills.',
'2025-10-20'
FROM student WHERE name='Eva Park';

-- Farah
INSERT INTO comments(student_id, body, created_at)
SELECT id,
'She has QA experience, provides clear bug reports, and displays strong attention to detail. She is interested in security testing and automation, and she consistently documents test cases for developers to follow easily.',
'2025-10-22'
FROM student WHERE name='Farah Ali';
