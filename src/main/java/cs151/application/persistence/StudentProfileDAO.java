package cs151.application.persistence;

import cs151.application.studentprofile.Student;
import cs151.application.studentprofile.StudentRow;

import java.sql.*;
import java.util.*;

public class StudentProfileDAO {
    // ---------- CREATE ----------
    /** Insert a student + skills + optional comments in one transaction. Returns id or -1. */
    public long insert(String name,
                       String academicStatus,
                       boolean employed,
                       String jobDetails,
                       String preferredRole,
                       boolean whitelist,
                       boolean isBlacklisted,
                       List<String> languages,
                       List<String> databases,
                       List<String> comments) {
        if (name == null || name.isBlank()) return -1; // minimal guard

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
                ps.setString(2, academicStatus); // assume normalized by service
                ps.setInt(3, employed ? 1 : 0);
                ps.setString(4, jobDetails == null ? "" : jobDetails.trim());
                ps.setString(5, preferredRole);
                ps.setInt(6, whitelist ? 1 : 0);
                ps.setInt(7, isBlacklisted ? 1 : 0);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) { c.rollback(); return -1; }
                    studentId = keys.getLong(1);
                }
            }

            if (languages != null) {
                try (PreparedStatement ps = c.prepareStatement(insertLang)) {
                    for (String s : languages) {
                        if (s == null || s.isBlank()) continue;
                        ps.setLong(1, studentId);
                        ps.setString(2, s);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            if (databases != null) {
                try (PreparedStatement ps = c.prepareStatement(insertDb)) {
                    for (String s : databases) {
                        if (s == null || s.isBlank()) continue;
                        ps.setLong(1, studentId);
                        ps.setString(2, s);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            if (comments != null) {
                try (PreparedStatement ps = c.prepareStatement(insertCmt)) {
                    for (String body : comments) {
                        if (body == null) continue;
                        String b = body.trim();
                        if (b.isEmpty()) continue;
                        ps.setLong(1, studentId);
                        ps.setString(2, b);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
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
                          String academicStatus,
                          boolean employed,
                          String jobDetails,
                          String preferredRole,
                          boolean whitelist,
                          boolean isBlacklisted,
                          List<String> languages,
                          List<String> databases,
                          List<String> comments) {
        if (id <= 0 || name == null || name.isBlank()) return false;

        final String updStudent = """
        UPDATE student 
        SET name=?, academic_status=?, employed=?, job_details=?, preferred_role=?, whitelist=?, isBlacklisted=?
        WHERE id=?
    """;

        final String delLangs = "DELETE FROM programming_languages WHERE student_id=?";
        final String delDbs   = "DELETE FROM databases WHERE student_id=?";
        final String delComments = "DELETE FROM comments WHERE student_id=?";

        final String insLang  = "INSERT OR IGNORE INTO programming_languages(student_id, language) VALUES (?,?)";
        final String insDb    = "INSERT OR IGNORE INTO databases(student_id, database_name) VALUES (?,?)";
        final String insCmt   = "INSERT INTO comments(student_id, body, created_at) VALUES (?, ?, datetime('now'))";

        try (Connection c = DatabaseConnector.getConnection()) {
            c.setAutoCommit(false);

            // Update student basic info
            try (PreparedStatement ps = c.prepareStatement(updStudent)) {
                ps.setString(1, name.trim());
                ps.setString(2, academicStatus);
                ps.setInt(3, employed ? 1 : 0);
                ps.setString(4, jobDetails == null ? "" : jobDetails.trim());
                ps.setString(5, preferredRole);
                ps.setInt(6, whitelist ? 1 : 0);
                ps.setInt(7, isBlacklisted ? 1 : 0);
                ps.setLong(8, id);

                if (ps.executeUpdate() == 0) { c.rollback(); return false; }
            }

            // Reset and update languages
            try (PreparedStatement ps = c.prepareStatement(delLangs)) { ps.setLong(1, id); ps.executeUpdate(); }
            if (languages != null) {
                try (PreparedStatement ps = c.prepareStatement(insLang)) {
                    for (String s : languages) {
                        if (s == null || s.isBlank()) continue;
                        ps.setLong(1, id);
                        ps.setString(2, s.trim());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // Reset and update databases
            try (PreparedStatement ps = c.prepareStatement(delDbs)) { ps.setLong(1, id); ps.executeUpdate(); }
            if (databases != null) {
                try (PreparedStatement ps = c.prepareStatement(insDb)) {
                    for (String s : databases) {
                        if (s == null || s.isBlank()) continue;
                        ps.setLong(1, id);
                        ps.setString(2, s.trim());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            // Optionally reset/add comments
            try (PreparedStatement psDel = c.prepareStatement(delComments)) {
                psDel.setLong(1, id);
                psDel.executeUpdate();
            }

            if (comments != null) {
                try (PreparedStatement ps = c.prepareStatement(insCmt)) {
                    for (String body : comments) {
                        if (body == null || body.isBlank()) continue;
                        ps.setLong(1, id);
                        ps.setString(2, body.trim());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
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
        SELECT id, name, academic_status, employed, job_details, 
               preferred_role, whitelist, isBlacklisted,
               languages, databases, commentsCount, lastComment
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
                        rs.getString("academic_status"),
                        rs.getInt("employed") == 1,
                        rs.getString("job_details"),
                        rs.getString("preferred_role"),
                        rs.getInt("whitelist") == 1,
                        rs.getInt("isBlacklisted") == 1,
                        rs.getString("languages"),
                        rs.getString("databases"),
                        rs.getInt("commentsCount"),
                        rs.getString("lastComment")
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

    public boolean existsByNameForOther(String name, long excludeId) {
        if (name == null || name.isBlank() || excludeId <= 0) return false;
        String sql = "SELECT 1 FROM student WHERE trim(name)=trim(?) COLLATE NOCASE AND id <> ? LIMIT 1";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setLong(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ---------- FIND BY EDIT ----------
    public Student findById(long id) {
        if (id <= 0) return null;

        String sqlStudent = """
            SELECT id, name, academic_status, employed, job_details,
                   preferred_role, whitelist, isBlacklisted
            FROM student
            WHERE id = ?
        """;

        String sqlLangs = "SELECT language FROM programming_languages WHERE student_id = ?";
        String sqlDbs   = "SELECT database_name FROM databases WHERE student_id = ?";
        String sqlCmts  = "SELECT body FROM comments WHERE student_id = ? ORDER BY created_at ASC";

        try (Connection c = DatabaseConnector.getConnection()) {

            Student s = null;
            try (PreparedStatement ps = c.prepareStatement(sqlStudent)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String academicStatus = rs.getString("academic_status");
                        boolean employed = rs.getInt("employed") == 1;
                        String jobDetails = rs.getString("job_details");
                        String role = rs.getString("preferred_role");
                        boolean whitelist = rs.getInt("whitelist") == 1;
                        boolean isBlacklisted = rs.getInt("isBlacklisted") == 1;

                        s = new Student(
                                name,
                                role,
                                academicStatus,
                                jobDetails,
                                isBlacklisted,
                                whitelist,
                                employed,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        );
                    }
                }
            }

            if (s == null) return null;

            try (PreparedStatement ps = c.prepareStatement(sqlLangs)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) s.getLanguages().add(rs.getString("language"));
                }
            }

            try (PreparedStatement ps = c.prepareStatement(sqlDbs)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) s.getStudentDbs().add(rs.getString("database_name"));
                }
            }

            try (PreparedStatement ps = c.prepareStatement(sqlCmts)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) s.getComments().add(rs.getString("body"));
                }
            }

            return s;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}