package cs151.application.persistence;

import cs151.application.persistence.StudentProfileDAO;
import cs151.application.studentprofile.StudentRow;

import java.util.Comparator;
import java.util.List;

// DAO for reports. Reuse StudentProfileDAO.
public class StudentReportDAO {
    private final StudentProfileDAO students = new StudentProfileDAO();

    public List<StudentRow> listAllNames() {
        return students.listAllForTable().stream()
                .map(r -> new StudentRow(r.getId(), r.getName()))
                .sorted(byName())
                .toList();
    }

    public List<StudentRow> listWhitelistNames() {
        return students.listAllForTable().stream()
                .filter(StudentRow::isWhitelist)
                .map(r -> new StudentRow(r.getId(), r.getName()))
                .sorted(byName())
                .toList();
    }

    public List<StudentRow> listBlacklistNames() {
        return students.listAllForTable().stream()
                .filter(StudentRow::isBlacklisted)
                .map(r -> new StudentRow(r.getId(), r.getName()))
                .sorted(byName())
                .toList();
    }

    private static Comparator<StudentRow> byName() {
        return Comparator.comparing(
                s -> s.getName() == null ? "" : s.getName(),
                String.CASE_INSENSITIVE_ORDER
        );
    }
}
