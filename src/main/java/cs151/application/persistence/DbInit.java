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

    public static void ensureSchema() {
        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);
            try (Statement s = c.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON");

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

                s.execute("""
                    CREATE TABLE IF NOT EXISTS programming_languages(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      student_id INTEGER NOT NULL
                        REFERENCES student(id) ON DELETE CASCADE,
                      language TEXT NOT NULL COLLATE NOCASE,
                      UNIQUE(student_id, language)
                    )
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS databases(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      student_id INTEGER NOT NULL
                        REFERENCES student(id) ON DELETE CASCADE,
                      database_name TEXT NOT NULL COLLATE NOCASE,
                      UNIQUE(student_id, database_name)
                    )
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS comments(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      student_id INTEGER NOT NULL
                        REFERENCES student(id) ON DELETE CASCADE,
                      body TEXT NOT NULL,
                      created_at TEXT NOT NULL DEFAULT (datetime('now'))
                    )
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS language_catalog(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      name TEXT NOT NULL COLLATE NOCASE UNIQUE
                    )
                """);
                seedDefaultLanguages(s);
                seedLanguageCatalogIfEmpty(c);

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

                s.execute("DROP INDEX IF EXISTS idx_student_name_nocase");
                s.execute("""
                   CREATE UNIQUE INDEX IF NOT EXISTS idx_student_name_unique
                   ON student( trim(name) COLLATE NOCASE )
                """);

                s.execute("CREATE INDEX IF NOT EXISTS idx_pl_sid ON programming_languages(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_db_sid ON databases(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_comments_sid ON comments(student_id)");
                s.execute("CREATE INDEX IF NOT EXISTS idx_comments_sid_created ON comments(student_id, datetime(created_at) DESC)");

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

    private static void ensureColumn(Connection c, String table, String column, String fullColumnDDL) throws SQLException {
        if (!hasColumn(c, table, column)) {
            try (Statement s = c.createStatement()) {
                s.execute("ALTER TABLE " + table + " ADD COLUMN " + fullColumnDDL);
            }
        }
    }

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

    public static void seedIfEmpty() {
        try (var c = DatabaseConnector.getConnection();
             var s = c.createStatement();
             var rs = s.executeQuery("SELECT COUNT(*) FROM student")) {

            if (rs.next() && rs.getInt(1) == 0) {
                runResource(c, "/cs151/application/database/seed-v07.sql");

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
            for (String stmt : sql.split(";")) {
                String trimmed = stmt.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--")) continue;
                try (var ps = c.prepareStatement(trimmed)) { ps.executeUpdate(); }
            }
        }
    }

    public static void seedOrCorrectToExactCounts() {
        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);
            try {
                ensureExactlyThreeLanguages(c);
                ensureExactlyFiveStudents(c);
                c.commit();
                System.out.println("[DB] Ensured exact counts.");
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ensureExactlyThreeLanguages(Connection c) throws SQLException {
        String[] must = {"C++", "Java", "Python"};
        int count = singleInt(c, "SELECT COUNT(*) FROM language_catalog");
        if (count != 3) {
            try (Statement s = c.createStatement()) {
                s.execute("DELETE FROM language_catalog");
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO language_catalog(name) VALUES (?)")) {
                for (String n : must) {
                    ps.setString(1, n);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    private static void ensureExactlyFiveStudents(Connection c) throws SQLException {
        try (Statement s = c.createStatement()) {
            s.execute("DELETE FROM comments");
            s.execute("DELETE FROM programming_languages");
            s.execute("DELETE FROM databases");
            s.execute("DELETE FROM student");
        }

        Object[][] rows = new Object[][]{
                {"Ava Chen",     "Junior",    0, "",               "Front-End", 0, 0},
                {"Brian Lee",    "Junior",    1, "Intern @XYZ",    "Back-End",  1, 0},
                {"Cam Nguyen",   "Graduate",  1, "Intern @ABC",    "Data",      1, 0},
                {"Diego Torres", "Sophomore", 1, "Part-time @DEF", "Full-Stack",0, 0},
                {"Eva Park",     "Senior",    1, "TA @SJSU",       "Other",     0, 1}
        };

        long[] ids = new long[rows.length];
        try (PreparedStatement ps = c.prepareStatement("""
            INSERT INTO student(name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted)
            VALUES (?,?,?,?,?,?,?)
        """, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < rows.length; i++) {
                Object[] r = rows[i];
                ps.setString(1, (String) r[0]);
                ps.setString(2, (String) r[1]);
                ps.setInt(3, (Integer) r[2]);
                ps.setString(4, (String) r[3]);
                ps.setString(5, (String) r[4]);
                ps.setInt(6, (Integer) r[5]);
                ps.setInt(7, (Integer) r[6]);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) ids[i] = k.getLong(1);
                }
            }
        }

        int cppId  = idForLang(c, "C++");
        int javaId = idForLang(c, "Java");
        int pyId   = idForLang(c, "Python");

        linkLang(c, ids[0], javaId);

        linkLang(c, ids[1], cppId);  linkLang(c, ids[1], javaId);
        linkLang(c, ids[2], javaId); linkLang(c, ids[2], pyId);
        linkLang(c, ids[3], cppId);  linkLang(c, ids[3], pyId);
        linkLang(c, ids[4], javaId);

        linkDb(c, ids[1], "MySQL");
        linkDb(c, ids[2], "PostgreSQL");
        linkDb(c, ids[3], "MongoDB");
    }

    private static int singleInt(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static int idForLang(Connection c, String name) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT id FROM language_catalog WHERE name=? COLLATE NOCASE")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private static void linkLang(Connection c, long studentId, int langId) throws SQLException {
        if (langId <= 0) return;
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES (?, (SELECT name FROM language_catalog WHERE id=?))")) {
            ps.setLong(1, studentId);
            ps.setInt(2, langId);
            ps.executeUpdate();
        }
    }

    private static void linkDb(Connection c, long studentId, String dbName) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT OR IGNORE INTO databases(student_id, database_name) VALUES (?,?)")) {
            ps.setLong(1, studentId);
            ps.setString(2, dbName);
            ps.executeUpdate();
        }
    }
}


