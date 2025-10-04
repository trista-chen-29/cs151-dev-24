package cs151.application.HomePage;

import cs151.application.ProgrammingLanguages.Language;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import cs151.application.Student;


public class HomePageController {

    private SearchController searchcontroller;

    public HomePageController(){
        searchcontroller = new SearchController();
    }

    public List<Student> search(String query, List<Language> programming_languages, List<String> databases, List<String> prof_interess, boolean blacklisted){
        return searchcontroller.filterstudent(query, programming_languages, databases, prof_interess, blacklisted);
    }

    public List<String> getProgrammingLanguages(){
        List<String> languages = new ArrayList<>();

        String sql = "SELECT DISTINCT language FROM programming_languages";

        try(Connection conn = DatabaseConnector.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(sql);
            ){

            while (res.next()){
                languages.add(res.getString("language"));
            }
        }

        catch(SQLException e){
            e.printStackTrace();
        }

        return languages;
    }

    public List<String> getDatabases(){
        List<String> databases = new ArrayList<>();
        databases.add("MySQL");
        databases.add("PostgreSQL");
        databases.add("MongoDB");
        databases.add("SQLite");

        return databases;
    }

    public List<String> getInterests(){
        List<String> interests = new ArrayList<>();
        interests.add("Fullstack Engineer");
        interests.add("Software Engineer");
        interests.add("AI/ML Engineer");
        interests.add("Data Scientist");

        return interests;
    }


}
