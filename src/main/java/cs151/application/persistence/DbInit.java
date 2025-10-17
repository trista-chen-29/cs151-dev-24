package cs151.application.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbInit {
    private DbInit() {}

    public static void ensureSchema() {
        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);
            try (Statement s = c.createStatement()) {
                // --- Tables ---
                s.execute("""
                    CREATE TABLE IF NOT EXISTS student(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      name TEXT NOT NULL,
                      isBlacklisted INTEGER NOT NULL DEFAULT 0
                    )
                """);

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

                s.execute("""
                    CREATE TABLE IF NOT EXISTS language_catalog(
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      name TEXT NOT NULL UNIQUE
                    )
                """);

                // --- Clean up existing case-insensitive duplicates ---
                // keep the first row (smallest rowid) per lower(name), delete the rest
                s.execute("""
                    DELETE FROM language_catalog
                    WHERE rowid NOT IN (
                      SELECT MIN(rowid)
                      FROM language_catalog
                      GROUP BY lower(name)
                    )
                """);

                // --- Recreate the case-insensitive unique index safely ---
                s.execute("DROP INDEX IF EXISTS idx_language_lower");
                s.execute("""
                    CREATE UNIQUE INDEX IF NOT EXISTS idx_language_lower
                    ON language_catalog(lower(name))
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
}