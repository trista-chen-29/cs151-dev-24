package cs151.application.persistence;

import cs151.application.studentprofile.StudentRow;

import java.sql.*;
import java.util.*;

public class StudentProfileDAO {
    // ---------- CREATE ----------
    /** Insert a student + skills + optional comments in one transaction. Returns id or -1. */
    public static long insert(String name,
                              String academicStatus,
                              boolean employed,
                              String jobDetails,
                              String preferredRole,
                              boolean whitelist,
                              boolean isBlacklisted,
                              List<String> languages,
                              List<String> databases,
                              List<String> comments) {

        if (name == null || name.isBlank()) return -1;
        if (academicStatus == null || academicStatus.isBlank()) academicStatus = "Freshman";
        if (preferredRole == null || preferredRole.isBlank()) preferredRole = "Other";

        // Normalize + de-dup each list by case-insensitive, preserve first appearance order
        List<String> langs = dedupCI(languages);
        List<String> dbs   = dedupCI(databases);

        List<String> cmts  = comments == null ? List.of() : comments;

        String insertStudent =
                "INSERT INTO student(name, academic_status, employed, job_details, preferred_role, whitelist, isBlacklisted) " +
                        "VALUES (?,?,?,?,?,?,?)";
        String insertLang = "INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES (?,?)";
        String insertDb   = "INSERT OR IGNORE INTO databases(student_id, database_name) VALUES (?,?)";
        String insertCmt  = "INSERT INTO comments(student_id, body, created_at) VALUES (?, ?, datetime('now'))";

        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);

            long studentId;
            try (PreparedStatement ps = c.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name.trim());
                ps.setString(2, academicStatus);
                ps.setInt(3, employed ? 1 : 0);
                ps.setString(4, (jobDetails == null ? "" : jobDetails.trim()));
                ps.setString(5, preferredRole);
                ps.setInt(6, whitelist ? 1 : 0);
                ps.setInt(7, isBlacklisted ? 1 : 0);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) { c.rollback(); return -1; }
                    studentId = keys.getLong(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement(insertLang)) {
                for (String s : langs) {
                    ps.setLong(1, studentId);
                    ps.setString(2, s);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = c.prepareStatement(insertDb)) {
                for (String s : dbs) {
                    ps.setLong(1, studentId);
                    ps.setString(2, s);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = c.prepareStatement(insertCmt)) {
                for (String body : cmts) {
                    String b = (body == null) ? "" : body.trim();
                    if (b.isEmpty()) continue;
                    ps.setLong(1, studentId);
                    ps.setString(2, b);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            c.commit();
            return studentId;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    // ---------- UPDATE ----------
    /** Replace student’s basic fields and reset their lists (simple approach for v0.5). */
    public boolean update(long id,
                          String name,
                          boolean isBlacklisted,
                          List<String> languages,
                          List<String> databases) {
        if (id <= 0 || name == null || name.isBlank()) return false;

        List<String> langs = dedupCI(languages);
        List<String> dbs   = dedupCI(databases);

        String upd = "UPDATE student SET name=?, isBlacklisted=? WHERE id=?";
        String delL = "DELETE FROM programming_languages WHERE student_id=?";
        String delD = "DELETE FROM databases WHERE student_id=?";
        String insL = "INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES (?,?)";
        String insD = "INSERT OR IGNORE INTO databases(student_id, database_name) VALUES (?,?)";

        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(upd)) {
                ps.setString(1, name.trim());
                ps.setInt(2, isBlacklisted ? 1 : 0);
                ps.setLong(3, id);
                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
            }

            try (PreparedStatement ps = c.prepareStatement(delL)) { ps.setLong(1, id); ps.executeUpdate(); }
            try (PreparedStatement ps = c.prepareStatement(delD)) { ps.setLong(1, id); ps.executeUpdate(); }

            try (PreparedStatement ps = c.prepareStatement(insL)) {
                for (String s : langs) { ps.setLong(1, id); ps.setString(2, s); ps.addBatch(); }
                ps.executeBatch();
            }
            try (PreparedStatement ps = c.prepareStatement(insD)) {
                for (String s : dbs) { ps.setLong(1, id); ps.setString(2, s); ps.addBatch(); }
                ps.executeBatch();
            }

            c.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ---------- DELETE ----------
    public boolean delete(long id) {
        if (id <= 0) return false;
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM student WHERE id=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0; // cascades to pivots
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ---------- READ for TableView ----------
    /** Returns rows pre-shaped for the JavaFX TableView, sorted A→Z case-insensitive by name. */
    public List<StudentRow> listAllForTable() {
        String sql = """
            SELECT id, name, isBlacklisted, languages, databases, commentsCount, lastComment
            FROM student_profile_view
            ORDER BY name COLLATE NOCASE ASC, id ASC
        """;
        List<StudentRow> out = new ArrayList<>();
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new StudentRow(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("isBlacklisted") == 1,
                        nvl(rs.getString("languages")),
                        nvl(rs.getString("databases")),
                        rs.getInt("commentsCount"),
                        nvl(rs.getString("lastComment"))
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    // ---------- Exists by name (for controller duplicate check) ----------
    public boolean existsByName(String name) {
        if (name == null || name.isBlank()) return false;
        String sql = "SELECT 1 FROM student WHERE trim(name) = trim(?) COLLATE NOCASE LIMIT 1";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ---------- Helpers ----------
    private static String nvl(String s) { return s == null ? "" : s; }

    private static List<String> dedupCI(List<String> in) {
        if (in == null) return List.of();
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        for (String raw : in) {
            if (raw == null) continue;
            String t = raw.trim();
            if (t.isEmpty()) continue;
            String key = t.toLowerCase();
            map.putIfAbsent(key, t);
        }
        return new ArrayList<>(map.values());
    }
}