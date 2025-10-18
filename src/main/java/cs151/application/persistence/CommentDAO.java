package cs151.application.persistence;

import cs151.application.studentprofile.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    public long add(long studentId, String body) {
        if (studentId <= 0) return -1;
        if (body == null || body.trim().isEmpty()) return -1;
        String sql = "INSERT INTO comments(student_id, body) VALUES (?, ?)";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, studentId);
            ps.setString(2, body.trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getLong(1) : -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Comment> listByStudent(long studentId) {
        String sql = """
      SELECT id, student_id, body, created_at
      FROM comments
      WHERE student_id = ?
      ORDER BY datetime(created_at) DESC, id DESC
    """;
        List<Comment> out = new ArrayList<>();
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Comment(
                            rs.getLong("id"),
                            rs.getLong("student_id"),
                            rs.getString("body"),
                            rs.getString("created_at")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    public boolean delete(long commentId) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
