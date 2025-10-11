package cs151.application.ProgrammingLanguages;

import cs151.application.HomePage.DatabaseConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgrammingLanguagesDAO {

    public ProgrammingLanguagesDAO() {
        ensureTable();
        deduplicateCaseInsensitive();
        ensureUniqueIndex();
    }

    private void ensureTable() {
        final String sql = "CREATE TABLE IF NOT EXISTS language_catalog (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL" +
                ")";
        try (Connection c = DatabaseConnector.getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void ensureUniqueIndex() {
        final String sql = "CREATE UNIQUE INDEX IF NOT EXISTS uq_language_name_nocase " +
                "ON language_catalog(name COLLATE NOCASE)";
        try (Connection c = DatabaseConnector.getConnection();
             Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deduplicateCaseInsensitive() {
        final String sql = "DELETE FROM language_catalog " +
                "WHERE id NOT IN ( " +
                "  SELECT MIN(id) FROM language_catalog GROUP BY LOWER(name) " +
                ")";
        try (Connection c = DatabaseConnector.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> listAll() {
        List<String> out = new ArrayList<>();
        String sql = "SELECT name FROM language_catalog " +
                "ORDER BY name COLLATE NOCASE, LENGTH(name)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    public boolean save(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return false;

        String sql = "INSERT OR IGNORE INTO language_catalog(name) VALUES (?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trimmed);
            return ps.executeUpdate() > 0; // false when duplicate (case-insensitive)
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(String name) {
        final String sql = "DELETE FROM language_catalog WHERE LOWER(name) = LOWER(?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public int deleteAll() {
        final String sql = "DELETE FROM language_catalog";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            return 0;
        }
    }
}
