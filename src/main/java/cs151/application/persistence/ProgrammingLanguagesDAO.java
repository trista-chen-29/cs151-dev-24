package cs151.application.persistence;

import cs151.application.programminglanguages.Language;

import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ProgrammingLanguagesDAO {

    /** Normalize for consistent storage & comparisons. */
    private static String normalize(String s) {
        if (s == null) return null;
        // NFC normalize, collapse internal spaces, trim
        String n = Normalizer.normalize(s, Normalizer.Form.NFC)
                .replaceAll("\\s+", " ")
                .trim();
        return n.isEmpty() ? null : n;
    }

    /** True if the name exists (case-insensitive). */
    public boolean existsIgnoreCase(String name) {
        String n = normalize(name);
        if (n == null) return false;

        String sql = "SELECT 1 FROM language_catalog WHERE name = ? COLLATE NOCASE LIMIT 1";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, n);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Insert; returns false if duplicate (case-insensitive) or invalid. */
    public boolean save(String name) {
        String n = normalize(name);
        if (n == null) return false;

        // INSERT OR IGNORE returns 0 rows if uniqueness (lower(name)) is violated
        String sql = "INSERT OR IGNORE INTO language_catalog(name) VALUES (?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, n);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Return names sorted A→Z (case-insensitive), lowercase first when only case differs. */
    public List<String> listAll() {
        List<String> out = new ArrayList<>();
        String sql = """
            SELECT name
            FROM language_catalog
            ORDER BY
              LOWER(name) ASC,
              CASE WHEN name = LOWER(name) THEN 0 ELSE 1 END,
              name ASC
        """;
        try (Connection c = DatabaseConnector.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /** TableView helper: build Language objects with the same ordering as listAll(). */
    public List<Language> listLanguageObjects() {
        List<Language> out = new ArrayList<>();
        for (String s : listAll()) out.add(new Language(s));
        return out;
    }

    /** Optional: convenience for your "Clear" test button. */
    public void clearAll() {
        try (Connection c = DatabaseConnector.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM language_catalog");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}