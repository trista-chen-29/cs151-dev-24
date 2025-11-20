package cs151.application.reports;

import java.util.List;

public interface ReportsProvider {
    enum Filter { ALL, WL, BL }
    List<StudentListItem> getStudents(Filter filter);
}
