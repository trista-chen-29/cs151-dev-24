package cs151.application.studentprofile;

import java.util.List;

public class Student {
    private int studentId;
    private String name;
    private String professionalRole;
    private String academicStatus;
    private String jobDetails;
    private boolean isBlackListed;
    private boolean isWhiteListed;
    private boolean employmentStatus;
    private List<String> languages;
    private List<String> studentDbs;
    private List<String> comments;

    /*
    Note: List<String> is a temporary solution (It works for now, but needs to be turned into a List<Language> data type.
     */

    //constructor to make SearchService happy
    public Student(int id, String name,boolean isBlackListed,List<String> languages,List<String> studentDbs, List<String> studentIds ){}

    public Student(String studentName, String professionalRole,String academicStatus, String jobDetails, boolean isBlacklisted,boolean isWhiteListed,
                   boolean employmentStatus,  List<String> languages, List<String> studentDbs, List<String> comments){
        this.name = studentName;
        this.jobDetails = jobDetails;
        this.academicStatus = academicStatus;
        this.isBlackListed = isBlacklisted;
        this.languages = languages;
        this.studentDbs = studentDbs;
        this.professionalRole = professionalRole;
        this.comments = comments;
        this.employmentStatus = employmentStatus;
        this.isWhiteListed = isWhiteListed;
    }
    public String getName(){return name;}
    public String getJobDetails(){return jobDetails;}
    public String getProfessionalRole(){return professionalRole;}
    public String getAcademicStatus(){return academicStatus;}
    public List<String> getLanguages(){return languages;}
    public List<String> getStudentDbs(){return studentDbs;}
    public List<String> getComments(){return comments;}
    public boolean getBlackList(){return isBlackListed;}
    public boolean getWhiteList(){return isWhiteListed;}
    public boolean getEmploymentStatus(){return employmentStatus;}
}