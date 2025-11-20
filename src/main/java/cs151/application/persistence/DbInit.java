package cs151.application.persistence;

import java.sql.*;

public class DbInit {
    private DbInit() {}

    public static void logDbLocation() {
        try (var c = DatabaseConnector.getConnection();
             var s = c.createStatement();
             var rs = s.executeQuery("PRAGMA database_list;")) {
            while (rs.next()) {
                System.out.println("[DB] " + rs.getString("name") + " => " + rs.getString("file"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Create tables, constraints & indexes if missing. Safe to call every startup. */
    public static void ensureSchema() {
        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);
            try (Statement s = c.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON");

                // --- Core tables ---

                // Students
                s.execute("""
                  CREATE TABLE IF NOT EXISTS student(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    academic_status TEXT NOT NULL DEFAULT 'Freshman',
                    employed INTEGER NOT NULL DEFAULT 0,
                    job_details TEXT,
                    preferred_role TEXT NOT NULL DEFAULT 'Other',
                    whitelist INTEGER NOT NULL DEFAULT 0,
                    isBlacklisted INTEGER NOT NULL DEFAULT 0,
                    CHECK (employed IN (0,1)),
                    CHECK (whitelist IN (0,1)),
                    CHECK (isBlacklisted IN (0,1)),
                    CHECK (NOT (whitelist = 1 AND isBlacklisted = 1)),
                    CHECK (academic_status IN ('Freshman','Sophomore','Junior','Senior','Graduate'))
                  )
                """);

                // Student's programming languages
                s.execute("""
                    CREATE TABLE IF NOT EXISTS programming_languages(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      student_id INTEGER NOT NULL
                        REFERENCES student(id) ON DELETE CASCADE,
                      language TEXT NOT NULL COLLATE NOCASE,
                      UNIQUE(student_id, language)
                    )
                """);

                // Student's databases
                s.execute("""
                    CREATE TABLE IF NOT EXISTS databases(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      student_id INTEGER NOT NULL
                        REFERENCES student(id) ON DELETE CASCADE,
                      database_name TEXT NOT NULL COLLATE NOCASE,
                      UNIQUE(student_id, database_name)
                    )
                """);

                // Comments
                s.execute("""
                    CREATE TABLE IF NOT EXISTS comments(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      student_id INTEGER NOT NULL
                        REFERENCES student(id) ON DELETE CASCADE,
                      body TEXT NOT NULL,
                      created_at TEXT NOT NULL DEFAULT (datetime('now'))
                    )
                """);

                // Language catalog for Define PL page
                s.execute("""
                    CREATE TABLE IF NOT EXISTS language_catalog(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      name TEXT NOT NULL COLLATE NOCASE UNIQUE
                    )
                """);
                seedDefaultLanguages(s);
                seedLanguageCatalogIfEmpty(c);

                // --- Upgrades for older DBs ---
                ensureColumn(c, "student", "academic_status",
                        "academic_status TEXT NOT NULL DEFAULT 'Freshman'");
                ensureColumn(c, "student", "employed",
                        "employed INTEGER NOT NULL DEFAULT 0");
                ensureColumn(c, "student", "job_details",
                        "job_details TEXT");
                ensureColumn(c, "student", "preferred_role",
                        "preferred_role TEXT NOT NULL DEFAULT 'Other'");
                ensureColumn(c, "student", "whitelist",
                        "whitelist INTEGER NOT NULL DEFAULT 0");
                ensureColumn(c, "student", "isBlacklisted",
                        "isBlacklisted INTEGER NOT NULL DEFAULT 0");

                // Unique full name (trimmed, case-insensitive)
                s.execute("DROP INDEX IF EXISTS idx_student_name_nocase");
                s.execute("""
                   CREATE UNIQUE INDEX IF NOT EXISTS idx_student_name_unique
                   ON student( trim(name) COLLATE NOCASE )
                """);

                // Helpful indexes
                s.execute("CREATE INDEX IF NOT EXISTS idx_pl_sid ON programming_languages(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_db_sid ON databases(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_comments_sid ON comments(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_comments_sid_created ON comments(student_id, datetime(created_at) DESC)");

                // View used by the TableView
                s.execute("DROP VIEW IF EXISTS student_profile_view");
                s.execute("""
                  CREATE VIEW student_profile_view AS
                  SELECT
                    s.id,
                    s.name,
                    s.academic_status,
                    s.employed,
                    s.job_details,
                    s.preferred_role,
                    s.whitelist,
                    s.isBlacklisted,
                    (
                      SELECT GROUP_CONCAT(x.language, ', ')
                      FROM (SELECT DISTINCT language
                            FROM programming_languages
                            WHERE student_id = s.id
                            ORDER BY LOWER(language)) x
                    ) AS languages,
                    (
                      SELECT GROUP_CONCAT(x.database_name, ', ')
                      FROM (SELECT DISTINCT database_name
                            FROM databases
                            WHERE student_id = s.id
                            ORDER BY LOWER(database_name)) x
                    ) AS databases,
                    (SELECT COUNT(*) FROM comments c WHERE c.student_id = s.id) AS commentsCount,
                    (SELECT body FROM comments c
                       WHERE c.student_id = s.id
                       ORDER BY datetime(c.created_at) DESC, c.id DESC
                       LIMIT 1) AS lastComment
                  FROM student s
                """);

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Add a column only if it is missing. */
    private static void ensureColumn(Connection c, String table, String column, String fullColumnDDL) throws SQLException {
        if (!hasColumn(c, table, column)) {
            try (Statement s = c.createStatement()) {
                s.execute("ALTER TABLE " + table + " ADD COLUMN " + fullColumnDDL);
            }
        }
    }

    /** Check PRAGMA table_info(table) for a column name. */
    private static boolean hasColumn(Connection c, String table, String column) throws SQLException {
        String sql = "PRAGMA table_info(" + table + ")";
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (column.equalsIgnoreCase(name)) return true;
            }
            return false;
        }
    }

    private static void seedDefaultLanguages(Statement s) throws SQLException {
        s.execute("""
            INSERT OR IGNORE INTO language_catalog(name) VALUES
            ('C++'), ('Java'), ('Python')
        """);
    }

    private static void seedLanguageCatalogIfEmpty(Connection c) throws SQLException {
        try (Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM language_catalog")) {
            int n = rs.next() ? rs.getInt(1) : 0;
            if (n == 0) {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO language_catalog(name) VALUES (?), (?), (?)")) {
                    ps.setString(1, "Java");
                    ps.setString(2, "Python");
                    ps.setString(3, "C++");
                    ps.executeUpdate();
                }
            }
        }
    }

    /** Seed initial data once when the database is empty. */
    public static void seedIfEmpty() {
        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);

            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM student")) {

                int count = rs.next() ? rs.getInt(1) : 0;
                System.out.println("[DB] student count before seed = " + count);

                if (count > 0) {
                    System.out.println("[DB] Seed skipped (data exists).");
                    return;
                }

                // Ensure catalog has core languages
                seedLanguageCatalogIfEmpty(c);

                // ---------- Insert 6 students ----------
                long aliceId = insertStudent(
                        c,
                        "Alice Chen",
                        "Senior",
                        true,
                        "Intern @ Acme",
                        "Front-End",
                        true,
                        false
                );

                long brianId = insertStudent(
                        c,
                        "Brian Lee",
                        "Junior",
                        false,
                        null,
                        "Back-End",
                        false,
                        true
                );

                long camId = insertStudent(
                        c,
                        "Cam Nguyen",
                        "Graduate",
                        true,
                        "TA - CS Dept.",
                        "Data",
                        true,
                        false
                );

                long diegoId = insertStudent(
                        c,
                        "Diego Torres",
                        "Sophomore",
                        false,
                        "Full-stack project helper",
                        "Full-Stack",
                        false,
                        true
                );

                long evaId = insertStudent(
                        c,
                        "Eva Park",
                        "Senior",
                        true,
                        "Open-source contributor",
                        "Other",
                        true,
                        false
                );

                long farahId = insertStudent(
                        c,
                        "Farah Ali",
                        "Junior",
                        true,
                        "Part-time QA assistant",
                        "QA",
                        false,
                        true
                );

                // ---------- Programming languages ----------
                addLanguage(c, aliceId, "Java");
                addLanguage(c, aliceId, "Python");
                addLanguage(c, aliceId, "C++");

                addLanguage(c, brianId, "C++");
                addLanguage(c, brianId, "Java");

                addLanguage(c, camId, "Python");
                addLanguage(c, camId, "Java");

                addLanguage(c, diegoId, "C++");
                addLanguage(c, diegoId, "Python");

                addLanguage(c, evaId, "Python");
                addLanguage(c, evaId, "Java");

                addLanguage(c, farahId, "Java");
                addLanguage(c, farahId, "C++");

                // ---------- Databases ----------
                addDatabase(c, aliceId, "MySQL");
                addDatabase(c, aliceId, "SQLite");

                addDatabase(c, brianId, "Postgres");
                addDatabase(c, brianId, "MySQL");

                addDatabase(c, camId, "MongoDB");
                addDatabase(c, camId, "Postgres");

                addDatabase(c, diegoId, "MySQL");
                addDatabase(c, diegoId, "SQL Server");

                addDatabase(c, evaId, "SQLite");
                addDatabase(c, evaId, "MongoDB");

                addDatabase(c, farahId, "MySQL");
                addDatabase(c, farahId, "SQLite");

                // ---------- Comments: at least 30 words each ----------
                addComment(c, aliceId,
                        "Alice consistently writes well-structured front-end components, actively discusses UI changes during team meetings, and updates her pull requests promptly after feedback. She explains her design decisions clearly and helps maintain consistent styling across the entire interface.");
                addComment(c, brianId,
                        "Brian often misses internal deadlines and sometimes pushes unfinished backend modules to the repository without testing, which causes frequent merge conflicts. He rarely responds to messages in the group chat, and his team members often have to redo his tasks or wait for last-minute updates, making collaboration difficult.");
                addComment(c, camId,
                        "Cam demonstrates strong understanding of data modeling and enjoys experimenting with database query optimization. He often helps classmates troubleshoot SQL mistakes and prepares simple diagrams to explain concepts like indexing and normalization.");
                addComment(c, diegoId,
                        "Although Diego has strong technical skills, he frequently ignores agreed-upon coding standards and refuses to document his work properly. His teammates have mentioned that he tends to work alone without communicating, which causes integration issues and delays during project milestones.");
                addComment(c, evaId,
                        "Eva documents meeting discussions clearly and reminds teammates about upcoming deadlines. She recently became interested in DevOps and successfully set up a basic CI pipeline that runs database migrations and triggers build testing on every pull request.");
                addComment(c, farahId,
                        "Farah has QA experience, but she sometimes submits incomplete bug reports without clear reproduction steps or severity levels, which slows down developers. She also occasionally skips team meetings without informing others, making it hard to plan sprint testing tasks.");

                c.commit();
                System.out.println("[DB] Seed inserted (6 students).");
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---- helper methods for seeding ----

    private static long insertStudent(Connection c,
                                      String name,
                                      String academicStatus,
                                      boolean employed,
                                      String jobDetails,
                                      String preferredRole,
                                      boolean whitelist,
                                      boolean blacklisted) throws SQLException {
        String sql = """
            INSERT INTO student
              (name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, academicStatus);
            ps.setInt(3, employed ? 1 : 0);
            if (jobDetails == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, jobDetails);
            }
            ps.setString(5, preferredRole);
            ps.setInt(6, whitelist ? 1 : 0);
            ps.setInt(7, blacklisted ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert student " + name);
    }

    private static void addLanguage(Connection c, long studentId, String language) throws SQLException {
        String sql = "INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setString(2, language);
            ps.executeUpdate();
        }
    }

    private static void addDatabase(Connection c, long studentId, String dbName) throws SQLException {
        String sql = "INSERT OR IGNORE INTO databases(student_id, database_name) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setString(2, dbName);
            ps.executeUpdate();
        }
    }

    private static void addComment(Connection c, long studentId, String body) throws SQLException {
        String sql = "INSERT INTO comments(student_id, body, created_at) VALUES (?, ?, date('now'))";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setString(2, body);
            ps.executeUpdate();
        }
    }
}
