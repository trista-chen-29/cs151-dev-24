PRAGMA foreign_keys = ON;

-- 保險：確保三個語言存在（與 Item 1 一致）
INSERT OR IGNORE INTO language_catalog(name) VALUES ('Java'),('Python'),('C++');

-- ===== 5 位學生（基礎資料）=====
INSERT OR IGNORE INTO student(name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
VALUES
('Alice Chen',   'Senior',   1, 'Intern @ Acme',   'Front-End', 1, 0),
('Brian Lee',    'Junior',   0, NULL,             'Back-End',  0, 0),
('Cam Nguyen',   'Graduate', 1, 'TA - CS Dept.',  'Data',      1, 0),
('Diego Torres', 'Sophomore',0, NULL,             'Full-Stack',0, 0),
('Eva Park',     'Senior',   0, NULL,             'Other',     0, 0);

-- ===== 技能（語言）=====
-- Alice
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Alice Chen';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Alice Chen';

-- Brian
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'C++'    FROM student WHERE name='Brian Lee';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Brian Lee';

-- Cam
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Cam Nguyen';
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Java'   FROM student WHERE name='Cam Nguyen';

-- Diego
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'C++'    FROM student WHERE name='Diego Torres';

-- Eva
INSERT OR IGNORE INTO programming_languages(student_id, language)
SELECT id, 'Python' FROM student WHERE name='Eva Park';

-- ===== 資料庫技能 =====
-- Alice
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MySQL'  FROM student WHERE name='Alice Chen';

-- Brian
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'Postgres' FROM student WHERE name='Brian Lee';

-- Cam
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MongoDB'  FROM student WHERE name='Cam Nguyen';

-- Diego
INSERT OR IGNORE INTO databases(student_id, database_name)
SELECT id, 'MySQL'    FROM student WHERE name='Diego Torres';

-- Eva（無）

-- ===== 範例留言（可選）=====
INSERT OR IGNORE INTO comments(student_id, body)
SELECT id, 'Great progress on data course.' FROM student WHERE name='Cam Nguyen';

INSERT OR IGNORE INTO comments(student_id, body)
SELECT id, 'Prefers backend tasks.' FROM student WHERE name='Brian Lee';
