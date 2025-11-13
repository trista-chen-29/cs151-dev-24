package cs151.application.studentprofile;

import cs151.application.persistence.CommentDAO;
import cs151.application.persistence.StudentProfileDAO;

import java.util.List;
import java.util.Locale;

public class ViewStudentProfileService {
    private final StudentProfileDAO students = new StudentProfileDAO();
    private final CommentDAO comments = new CommentDAO();

    public enum FilterMode { ALL, WL, BL }

    /** Find rows with WL/BL + text search applied. */
    public List<StudentRow> find(String query, FilterMode mode) {
        final String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return students.listAllForTable().stream()
                .filter(r -> switch (mode) {
                    case WL -> r.isWhitelist();
                    case BL -> r.isBlacklisted();
                    default -> true;
                })
                .filter(r -> q.isEmpty() || contains(r.getName(), q)
                        || contains(r.getAcademicStatus(), q)
                        || contains(r.getPreferredRole(), q)
                        || contains(r.getLanguages(), q)
                        || contains(r.getDatabases(), q))
                .toList();
    }

    public List<Comment> listComments(long studentId) {
        return comments.listByStudent(studentId);
    }

    public boolean deleteStudent(long id) {
        return students.delete(id);
    }

    private static boolean contains(String s, String q) {
        return s != null && s.toLowerCase(Locale.ROOT).contains(q);
    }
}
