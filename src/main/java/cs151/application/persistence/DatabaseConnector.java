package cs151.application.persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnector {
    private static final String DEFAULT_DIR = System.getProperty("user.home") + "/.profsupport";
    private static final String DB_FILE_NAME = "profsupport.db";

    private DatabaseConnector() {}

    /**
     * Build a JDBC URL with simple overrides:
     * -Dprofsupport.db.dir=/some/dir      -> file DB in that dir
     * -Dprofsupport.db.inMemory=true      -> shared in-memory DB (for tests)
     * (env PROFSUPPORT_DB_DIR works too)
     */
    private static String jdbcUrl() {
        // In-memory for tests/CI
        if ("true".equalsIgnoreCase(System.getProperty("profsupport.db.inMemory", "false"))) {
            return "jdbc:sqlite:file:memdb1?mode=memory&cache=shared";
        }

        // Directory override via sysprop or env
        String dir = System.getProperty(
                "profsupport.db.dir",
                System.getenv().getOrDefault("PROFSUPPORT_DB_DIR", DEFAULT_DIR)
        );

        ensureDir(dir);
        return "jdbc:sqlite:" + dir + "/" + DB_FILE_NAME;
    }

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(jdbcUrl());
        // sensible defaults for SQLite
        try (var st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");   // enforce FK constraints
            st.execute("PRAGMA busy_timeout = 5000"); // avoid 'database is locked' flakiness
            st.execute("PRAGMA journal_mode = WAL");  // better concurrency
            st.execute("PRAGMA synchronous = NORMAL");// decent durability + speed
        }
        return c;
    }

    private static void ensureDir(String dir) {
        try {
            Files.createDirectories(Path.of(dir));
        } catch (Exception ignore) {}
    }
}