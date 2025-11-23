package cs151.application.persistence;

import cs151.application.studentprofile.Comment;
import cs151.application.studentprofile.StudentRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * DAO for read-only student reports.
 * Provides filtered lists with search capability without any modification operations.
 */
public class StudentReportDAO {

    /**
     * Find all students matching the search query.
     * @param query Search text (case-insensitive, searches name, status, role, languages, databases)
     * @return List of matching student rows
     */
    public List<StudentRow> findAllStudents(String query) {
        String sql = """
        SELECT id, name, academic_status, employed, job_details, 
               preferred_role, whitelist, isBlacklisted,
               languages, databases
        FROM student_profile_view
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """;

        List<StudentRow> all = executeQuery(sql);
        return filterByQuery(all, query);
    }

    /**
     * Find whitelisted students matching the search query.
     * @param query Search text
     * @return List of matching whitelisted student rows
     */
    public List<StudentRow> findWhitelistStudents(String query) {
        String sql = """
        SELECT id, name, academic_status, employed, job_details, 
               preferred_role, whitelist, isBlacklisted,
               languages, databases
        FROM student_profile_view
        WHERE whitelist = 1
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """;

        List<StudentRow> all = executeQuery(sql);
        return filterByQuery(all, query);
    }

    /**
     * Find blacklisted students matching the search query.
     * @param query Search text
     * @return List of matching blacklisted student rows
     */
    public List<StudentRow> findBlacklistStudents(String query) {
        String sql = """
        SELECT id, name, academic_status, employed, job_details, 
               preferred_role, whitelist, isBlacklisted,
               languages, databases
        FROM student_profile_view
        WHERE isBlacklisted = 1
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """;

        List<StudentRow> all = executeQuery(sql);
        return filterByQuery(all, query);
    }

    /**
     * Get all comments for a specific student.
     * @param studentId The student's ID
     * @return List of comments ordered by creation date (newest first)
     */
    public List<Comment> getCommentsForStudent(long studentId) {
        String sql = """
        SELECT id, student_id, body, created_at
        FROM comments
        WHERE student_id = ?
        ORDER BY created_at DESC
        """;

        List<Comment> comments = new ArrayList<>();
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment(
                            rs.getLong("id"),
                            rs.getLong("student_id"),
                            rs.getString("body"),
                            rs.getString("created_at")
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return comments;
    }

    /**
     * Returns simple name list for all students (for summary reports).
     */
    public List<StudentRow> listAllNames() {
        return findAllStudents("").stream()
                .map(r -> new StudentRow(r.getId(), r.getName()))
                .sorted(byName())
                .toList();
    }

    /**
     * Returns simple name list for whitelisted students.
     */
    public List<StudentRow> listWhitelistNames() {
        return findWhitelistStudents("").stream()
                .map(r -> new StudentRow(r.getId(), r.getName()))
                .sorted(byName())
                .toList();
    }

    /**
     * Returns simple name list for blacklisted students.
     */
    public List<StudentRow> listBlacklistNames() {
        return findBlacklistStudents("").stream()
                .map(r -> new StudentRow(r.getId(), r.getName()))
                .sorted(byName())
                .toList();
    }

    /**
     * Helper method to execute queries and map results to StudentRow objects.
     */
    private List<StudentRow> executeQuery(String sql) {
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
                        rs.getString("databases")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    /**
     * Filter student rows by search query.
     * Searches across name, academic status, role, languages, and databases.
     */
    private List<StudentRow> filterByQuery(List<StudentRow> rows, String query) {
        if (query == null || query.trim().isEmpty()) {
            return rows;
        }

        final String q = query.trim().toLowerCase(Locale.ROOT);
        return rows.stream()
                .filter(r -> contains(r.getName(), q)
                        || contains(r.getAcademicStatus(), q)
                        || contains(r.getPreferredRole(), q)
                        || contains(r.getLanguages(), q)
                        || contains(r.getDatabases(), q))
                .toList();
    }

    private static boolean contains(String s, String q) {
        return s != null && s.toLowerCase(Locale.ROOT).contains(q);
    }

    private static Comparator<StudentRow> byName() {
        return Comparator.comparing(
                s -> s.getName() == null ? "" : s.getName(),
                String.CASE_INSENSITIVE_ORDER
        );
    }
}