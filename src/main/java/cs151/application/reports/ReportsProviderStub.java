package cs151.application.reports;

import java.util.ArrayList;
import java.util.List;

public final class ReportsProviderStub implements ReportsProvider {
    private static final ReportsProviderStub INSTANCE = new ReportsProviderStub();
    public static ReportsProviderStub getInstance() { return INSTANCE; }
    private ReportsProviderStub() {}

    @Override
    public List<StudentListItem> getStudents(Filter filter) {
        // Return empty list by default; teammate wires real data here.
        return new ArrayList<>();
    }
}
