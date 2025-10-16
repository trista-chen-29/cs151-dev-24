package cs151.application.homepage;

import cs151.application.persistence.DatabaseConnector;
import cs151.application.programminglanguages.Language;
import cs151.application.studentprofile.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class HomePageService {
    private final SearchService searchService;

    public HomePageService(){
        searchService = new SearchService();
    }

    public List<Student> search(String query,
                                List<Language> programming_languages,
                                List<String> databases,
                                List<String> prof_interest,
                                boolean blacklistedAllowed) {
        return searchService.filterstudent(query, programming_languages, databases, prof_interest, blacklistedAllowed);
    }

    public List<String> getProgrammingLanguages() {
        try (var c = DatabaseConnector.getConnection();
             var s = c.createStatement();
             var rs = s.executeQuery("SELECT name FROM language_catalog ORDER BY name")) {
            List<String> out = new ArrayList<>();
            while (rs.next()) out.add(rs.getString("name"));
            return out;
        } catch (SQLException e) { e.printStackTrace(); }
        return List.of();
    }

    // (Optional) keep these helpers if you still want static lists:
    public List<String> getDatabases() {
        return List.of("MySQL", "PostgreSQL", "MongoDB", "SQLite");
    }

    public List<String> getInterests() {
        return List.of("Fullstack Engineer", "Software Engineer", "AI/ML Engineer", "Data Scientist");
    }
}