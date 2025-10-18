package cs151.application.studentprofile;

public class StudentRow {
    private final long id;
    private final String name;
    private final boolean blacklisted;
    private final String languages;
    private final String databases;
    private final int commentsCount;
    private final String lastComment;

    public StudentRow(long id, String name, boolean blacklisted,
                      String languages, String databases,
                      int commentsCount, String lastComment) {
        this.id = id;
        this.name = name;
        this.blacklisted = blacklisted;
        this.languages = languages;
        this.databases = databases;
        this.commentsCount = commentsCount;
        this.lastComment = lastComment;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public boolean isBlacklisted() { return blacklisted; }
    public String getLanguages() { return languages; }
    public String getDatabases() { return databases; }
    public int getCommentsCount() { return commentsCount; }
    public String getLastComment() { return lastComment; }
}
