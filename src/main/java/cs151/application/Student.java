package cs151.application;

import cs151.application.ProgrammingLanguages.Language;
import java.util.List;

public class Student {
    private int studentId;
    private String name;
    private boolean isBlacklisted;
    private List<String> languages;
    private List<String> studentDbs;
    private List<String> studentInterests;

    /*
    Note: List<String> is a temporary solution (It works for now, but needs to be turned into a List<Language> data type.
     */
    public Student(int studentId, String studentName, Boolean isBlacklisted, List<String> languages, List<String> studentDbs, List<String> studentInterests){
        this.studentId = studentId;
        this.name = studentName;
        this.isBlacklisted = isBlacklisted;
        this.languages = languages;
        this.studentDbs = studentDbs;
        this.studentInterests = studentInterests;
    }
}