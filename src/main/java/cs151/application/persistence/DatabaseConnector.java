package cs151.application.persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnector {
    private static final String DB_DIR  = System.getProperty("user.home") + "/.profsupport";
    private static final String DB_PATH = DB_DIR + "/profsupport.db";
    private static final String URL     = "jdbc:sqlite:" + DB_PATH;

    private DatabaseConnector() {}

    public static Connection getConnection() throws SQLException {
        ensureDir();
        return DriverManager.getConnection(URL);  // new connection per call (safe with try-with-resources)
    }

    private static void ensureDir() {
        try { Files.createDirectories(Path.of(DB_DIR)); } catch (Exception ignore) {}
    }
}
