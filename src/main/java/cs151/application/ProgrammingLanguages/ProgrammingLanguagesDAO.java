package cs151.application.ProgrammingLanguages;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProgrammingLanguagesDAO {

    private static final String DB_URL = "jdbc:sqlite:data/project4.db";

    // lower-first ordering: a..z come before A..Z; primary key is case-insensitive order
    private static final Comparator<String> LOWER_FIRST = (a, b) -> {
        int ci = a.compareToIgnoreCase(b);
        if (ci != 0) return ci; // alphabetical ignoring case
        // same letters ignoring case -> prefer lowercase before UPPERCASE
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            char ca = a.charAt(i), cb = b.charAt(i);
            if (Character.toLowerCase(ca) != Character.toLowerCase(cb)) break;
            if (ca != cb) {
                if (Character.isLowerCase(ca) && Character.isUpperCase(cb)) return -1;
                if (Character.isUpperCase(ca) && Character.isLowerCase(cb)) return 1;
            }
        }
        return a.compareTo(b); // fallback
    };

    public ProgrammingLanguagesDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        // use a dedicated catalog; UNIQUE NOCASE guarantees case-insensitive uniqueness
        String sql = """
                CREATE TABLE IF NOT EXISTS language_catalog(
                    id   INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE COLLATE NOCASE
                )
                """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ignored) { }
    }

    private Connection connect() throws SQLException {
        try { java.nio.file.Files.createDirectories(java.nio.file.Path.of("data")); } catch (Exception ignored) { }
        return DriverManager.getConnection(DB_URL);
    }

    /** Returns true if a row exists with the same name (case-insensitive). */
    public boolean existsIgnoreCase(String name) {
        if (name == null) return false;
        String sql = "SELECT 1 FROM language_catalog WHERE name = ? COLLATE NOCASE LIMIT 1";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ignored) { return false; }
    }

    /** Inserts a name; returns false if it already exists (case-insensitive). */
    public boolean insert(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        // short-circuit to give UI a clean "duplicate" signal before touching DB
        if (existsIgnoreCase(name)) return false;

        String sql = "INSERT OR IGNORE INTO language_catalog(name) VALUES(?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) { return false; }
    }

    /** Returns all names sorted: a..z before A..Z. */
    public List<String> getAll() {
        List<String> out = new ArrayList<>();
        // get raw values; we’ll do "lower-first" ordering in Java for portability
        String sql = "SELECT name FROM language_catalog";
        try (Connection conn = connect(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(rs.getString("name"));
        } catch (SQLException ignored) { }
        out.sort(LOWER_FIRST);
        return out;
    }

    public boolean delete(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        String sql = "DELETE FROM language_catalog WHERE name = ? COLLATE NOCASE";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException ignored) { return false; }
    }

    public void clearAll() {
        String sql = "DELETE FROM language_catalog";
        try (Connection conn = connect(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException ignored) { }
    }

    /** Legacy helper your tests might call. */
    public void deleteAll() { clearAll(); }
}
