package cs151.application;

import cs151.application.ProgrammingLanguages.Language;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private int studentId;
    private String name;
    private boolean isBlacklisted;
    private List<Language> languages;
    private List<String> studentDbs;
    private List<String> studentInterests;

    /*
    Note: List<String> is a temporary solution (It works for now, but needs to be turned into a List<Language> data type.
     */
    public Student(int studentId, String studentName, Boolean isBlacklisted, List<Language> languages, List<String> studentDbs, List<String> studentInterests){
        this.studentId = studentId;
        this.name = studentName;
        this.isBlacklisted = isBlacklisted;
        this.languages = languages;
        this.studentDbs = studentDbs;
        this.studentInterests = studentInterests;
    }

    public String getName() { return name; }
    public boolean isBlacklisted() { return isBlacklisted; }
    public List<String> getProgrammingLanguages() {
        List<String> programmingLanguages = new ArrayList<>();
        for (Language language : languages) {
            programmingLanguages.add(language.getLanguageName());
        }
        return programmingLanguages;
    }
    public List<String> getDatabases() { return studentDbs; }
    public List<String> getInterests() { return studentInterests; }
}
