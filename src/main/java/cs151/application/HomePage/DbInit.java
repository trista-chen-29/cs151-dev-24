package cs151.application.HomePage;

import java.sql.Connection;
import java.sql.Statement;

public class DbInit {
    private DbInit() {}

    public static void ensureSchema() {
        try (Connection c = DatabaseConnector.getConnection(); Statement s = c.createStatement()) {
            // Students
            s.execute("""
                CREATE TABLE IF NOT EXISTS student(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  isBlacklisted INTEGER NOT NULL DEFAULT 0
                )
            """);

            // Student attributes used by SearchController
            s.execute("""
                CREATE TABLE IF NOT EXISTS programming_languages(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  student_id INTEGER NOT NULL,
                  language TEXT NOT NULL
                )
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS databases(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  student_id INTEGER NOT NULL,
                  database_name TEXT NOT NULL
                )
            """);
            s.execute("""
                CREATE TABLE IF NOT EXISTS interests(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  student_id INTEGER NOT NULL,
                  interest TEXT NOT NULL
                )
            """);

            // A master list for your Define-Language screen
            s.execute("""
                CREATE TABLE IF NOT EXISTS language_catalog(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL UNIQUE
                )
            """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
