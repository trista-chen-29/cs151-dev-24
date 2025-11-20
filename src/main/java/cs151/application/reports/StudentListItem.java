package cs151.application.reports;

public class StudentListItem {
    private final long id;
    private final String name;

    public StudentListItem(long id, String name) {
        this.id = id;
        this.name = name == null ? "" : name;
    }

    public long getId() { return id; }
    public String getName() { return name; }
}