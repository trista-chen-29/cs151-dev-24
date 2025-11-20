package cs151.application.reports;

import cs151.application.persistence.StudentReportDAO;
import cs151.application.studentprofile.StudentRow;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReportsService {
    public enum Filter { ALL, WHITELIST, BLACKLIST }

    private final StudentReportDAO dao = new StudentReportDAO();

    public List<StudentRow> listNames(Filter f) {
        return switch (f) {
            case WHITELIST -> dao.listWhitelistNames();
            case BLACKLIST -> dao.listBlacklistNames();
            default        -> dao.listAllNames();
        };
    }

    /** Writes a CSV with a single NAME column; returns file path. */
    public String exportNamesCsv(List<StudentRow> rows) throws Exception {
        Path tmp = Files.createTempFile("student-report-", ".csv");
        try (FileWriter w = new FileWriter(tmp.toFile())) {
            w.write("Name\n");
            for (StudentRow r : rows) {
                String name = (r == null || r.getName() == null) ? "" : r.getName();
                // simple CSV escaping for quotes
                w.write("\"" + name.replace("\"", "\"\"") + "\"\n");
            }
        }
        return tmp.toAbsolutePath().toString();
    }

    public void writeNamesCsv(List<StudentRow> rows, java.nio.file.Path dest) throws java.io.IOException {
        try (var w = java.nio.file.Files.newBufferedWriter(dest)) {
            w.write("Name\n");
            for (var r : rows) {
                var name = (r != null && r.getName() != null) ? r.getName() : "";
                // escape quotes
                w.write("\"" + name.replace("\"", "\"\"") + "\"\n");
            }
        }
    }
}
