package cs151.application.reports;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReportsExporter {
    public void exportCsv(File file, List<StudentListItem> rows) throws Exception {
        try (var fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            fw.write("Name\n");
            for (StudentListItem r : rows) {
                // basic CSV escaping for commas/quotes
                String name = r.getName().replace("\"", "\"\"");
                fw.write("\"" + name + "\"\n");
            }
        }
    }
}
