package cs151.application.homepage;

import cs151.application.persistence.DatabaseConnector;
import cs151.application.programminglanguages.Language;
import cs151.application.studentprofile.Student;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class SearchService {
    /**
     * Filters students by name, languages, databases, interests.
     * @param blacklistedAllowed true = include blacklisted students; false = exclude them
     */
    public List<Student> filterstudent(String searchQuery,
                                       List<Language> Languages,
                                       List<String> Databases,
                                       List<String> Interests,
                                       Boolean blacklistedAllowed) {

        List<Student> result = new ArrayList<>();

        // Normalize requested filters to lowercase Sets (handles nulls)
        Set<String> wantLangs = (Languages == null ? Set.<String>of()
                : Languages.stream()
                .map(Language::getLanguageName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet()));

        Set<String> wantDbs = (Databases == null ? Set.<String>of()
                : Databases.stream()
                .filter(Objects::nonNull)
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet()));

        Set<String> wantInts = (Interests == null ? Set.<String>of()
                : Interests.stream()
                .filter(Objects::nonNull)
                .map(String::trim).filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet()));

        try (Connection conn = DatabaseConnector.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT id, name, isBlacklisted FROM student WHERE 1=1");

            List<Object> params = new ArrayList<>();

            if (searchQuery != null && !searchQuery.isBlank()) {
                sql.append(" AND name LIKE ?");
                params.add("%" + searchQuery.trim() + "%"); // <-- wildcard search
            }
            // Only add a predicate when we want to EXCLUDE blacklisted
            if (Boolean.FALSE.equals(blacklistedAllowed)) {
                sql.append(" AND isBlacklisted = 0");
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int studentId       = rs.getInt("id");
                        String studentName  = rs.getString("name");
                        boolean blacklisted = rs.getInt("isBlacklisted") == 1;

                        // Fetch associated attributes
                        List<String> studentLangs = getStudentAttributes(conn, "programming_languages", "language", studentId);
                        List<String> studentDbs   = getStudentAttributes(conn, "databases", "database_name", studentId);
                        List<String> studentInts  = getStudentAttributes(conn, "interests", "interest", studentId);

                        // Apply list filters (case-insensitive) only if user requested some
                        if (!wantLangs.isEmpty() && studentLangs.stream()
                                .map(String::toLowerCase).noneMatch(wantLangs::contains)) continue;

                        if (!wantDbs.isEmpty() && studentDbs.stream()
                                .map(String::toLowerCase).noneMatch(wantDbs::contains)) continue;

                        if (!wantInts.isEmpty() && studentInts.stream()
                                .map(String::toLowerCase).noneMatch(wantInts::contains)) continue;

                        // Extra guard: if we’re excluding blacklisted, skip any that slipped through
                        if (Boolean.FALSE.equals(blacklistedAllowed) && blacklisted) continue;

                        // NOTE: ensure your Student constructor matches these fields
                        result.add(new Student(studentId, studentName, blacklisted,
                                studentLangs, studentDbs, studentInts));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /** Helper: fetch a student's attribute list from a child table. */
    private List<String> getStudentAttributes(Connection conn, String table, String column, int studentId) throws SQLException {
        List<String> attributes = new ArrayList<>();
        String sql = "SELECT " + column + " FROM " + table + " WHERE student_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) attributes.add(rs.getString(column));
            }
        }
        return attributes;
    }
}