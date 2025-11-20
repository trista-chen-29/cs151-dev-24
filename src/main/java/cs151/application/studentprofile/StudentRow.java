package cs151.application.studentprofile;

public class StudentRow {
    private final long id;
    private final String name;

    private final String academicStatus;
    private final boolean employed;
    private final String jobDetails;
    private final String preferredRole;

    private final boolean whitelist;
    private final boolean blacklisted;

    private final String languages;
    private final String databases;

    public StudentRow(long id, String name,
                      String academicStatus,
                      boolean employed,
                      String jobDetails,
                      String preferredRole,
                      boolean whitelist,
                      boolean blacklisted,
                      String languages,
                      String databases) {
        this.id = id;
        this.name = name;
        this.academicStatus = academicStatus;
        this.employed = employed;
        this.jobDetails = jobDetails;
        this.preferredRole = preferredRole;
        this.whitelist = whitelist;
        this.blacklisted = blacklisted;
        this.languages = languages;
        this.databases = databases;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getAcademicStatus() { return academicStatus; }
    public boolean isEmployed() { return employed; }
    public String getJobDetails() { return jobDetails == null ? "" : jobDetails; }
    public String getPreferredRole() { return preferredRole; }
    public boolean isWhitelist() { return whitelist; }
    public boolean isBlacklisted() { return blacklisted; }
    public String getLanguages() { return languages == null ? "" : languages; }
    public String getDatabases() { return databases == null ? "" : databases; }

    // lightweight constructor
    public StudentRow(long id, String name) {
        this(id, name,
                null,          // academicStatus
                false,         // employed
                null,          // jobDetails
                null,          // preferredRole
                false,         // whitelist
                false,         // blacklisted
                null,          // languages
                null);          // databases
    }
}