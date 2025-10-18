package cs151.application.studentprofile;

public class Comment {
    private final long id;
    private final long studentId;
    private final String body;
    private final String createdAt; // ISO-8601 string from SQLite

    public Comment(long id, long studentId, String body, String createdAt) {
        this.id = id; this.studentId = studentId; this.body = body; this.createdAt = createdAt;
    }
    public long getId() { return id; }
    public long getStudentId() { return studentId; }
    public String getBody() { return body; }
    public String getCreatedAt() { return createdAt; }
}
