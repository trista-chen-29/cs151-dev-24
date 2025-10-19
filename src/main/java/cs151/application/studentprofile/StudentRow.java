package cs151.application.studentprofile;

public class StudentRow {
    private final long id;
    private final String name;

    private final String academicStatus;
    private final boolean employed;
    private final String preferredRole;

    private final boolean whitelist;
    private final boolean blacklisted;

    private final String languages;
    private final String databases;

    private final int commentsCount;
    private final String lastComment;

    public StudentRow(long id, String name,
                      String academicStatus, boolean employed, String preferredRole,
                      boolean whitelist, boolean blacklisted,
                      String languages, String databases,
                      int commentsCount, String lastComment) {
        this.id = id;
        this.name = name;
        this.academicStatus = academicStatus;
        this.employed = employed;
        this.preferredRole = preferredRole;
        this.whitelist = whitelist;
        this.blacklisted = blacklisted;
        this.languages = languages;
        this.databases = databases;
        this.commentsCount = commentsCount;
        this.lastComment = lastComment;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getAcademicStatus() { return academicStatus; }
    public boolean isEmployed() { return employed; }
    public String getPreferredRole() { return preferredRole; }
    public boolean isWhitelist() { return whitelist; }
    public boolean isBlacklisted() { return blacklisted; }
    public String getLanguages() { return languages == null ? "" : languages; }
    public String getDatabases() { return databases == null ? "" : databases; }
    public int getCommentsCount() { return commentsCount; }
    public String getLastComment() { return lastComment == null ? "" : lastComment; }
}