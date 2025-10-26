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
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Create tables, constraints & indexes if missing. Safe to call every startup. */
    public static void ensureSchema() {
        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);
            try (Statement s = c.createStatement()) {
                // Always keep FKs on
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

                // Student's programming languages (case-insensitive + unique per student)
                s.execute("""
                    CREATE TABLE IF NOT EXISTS programming_languages(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      student_id INTEGER NOT NULL
                        REFERENCES student(id) ON DELETE CASCADE,
                      language TEXT NOT NULL COLLATE NOCASE,
                      UNIQUE(student_id, language)
                    )
                """);

                // Student's databases (case-insensitive + unique per student)
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

                // Define-PL master list — now directly NOCASE UNIQUE (no manual index needed)
                s.execute("""
                    CREATE TABLE IF NOT EXISTS language_catalog(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      name TEXT NOT NULL COLLATE NOCASE UNIQUE
                    )
                """);
                seedDefaultLanguages(s);
                seedLanguageCatalogIfEmpty(c);

                // --- Upgrades for older DBs (no IF NOT EXISTS in ALTER) ---
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

                // --- Make full name unique, case-insensitive, ignoring extra spaces ---
                // (Drop old non-unique index if it exists, then create the correct one.)
                s.execute("DROP INDEX IF EXISTS idx_student_name_nocase");
                s.execute("""
                   CREATE UNIQUE INDEX IF NOT EXISTS idx_student_name_unique
                   ON student( trim(name) COLLATE NOCASE )
                """);

                // --- Helpful FKs lookup indexes ---
                s.execute("CREATE INDEX IF NOT EXISTS idx_pl_sid ON programming_languages(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_db_sid ON databases(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_comments_sid ON comments(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_comments_sid_created ON comments(student_id, datetime(created_at) DESC)");

                // --- View for TableView (one row per student, lists are comma-joined & A→Z) ---
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

    /** Add a column only if it is missing. No constraints in ALTER (SQLite limitation). */
    private static void ensureColumn(Connection c, String table, String column, String fullColumnDDL) throws SQLException {
        if (!hasColumn(c, table, column)) {
            try (Statement s = c.createStatement()) {
                s.execute("ALTER TABLE " + table + " ADD COLUMN " + fullColumnDDL);
            }
        }
    }

    /** Checks PRAGMA table_info(table) for a column name. */
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
        // Will not duplicate thanks to UNIQUE NOCASE on name
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

    public static void seedIfEmpty() {
        try (var c = DatabaseConnector.getConnection();
             var s = c.createStatement();
             var rs = s.executeQuery("SELECT COUNT(*) FROM student")) {

            if (rs.next() && rs.getInt(1) == 0) {
                runResource(c, "/cs151/application/database/seed-v06.sql");
                System.out.println("[DB] Seed loaded.");
            } else {
                System.out.println("[DB] Seed skipped (data exists).");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runResource(Connection c, String path) throws Exception {
        try (var in = DbInit.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("Seed not found on classpath: " + path);
            String sql = new String(in.readAllBytes());
            // very simple splitter; good enough for our seed file
            for (String stmt : sql.split(";")) {
                String trimmed = stmt.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
                try (var ps = c.prepareStatement(trimmed)) { ps.executeUpdate(); }
            }
        }
    }
}