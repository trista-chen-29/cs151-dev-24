package cs151.application.HomePage;

import cs151.application.ProgrammingLanguages.Language;
import cs151.application.ProgrammingLanguages.PLController;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
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

    public List<Student> search(String query, List<String> programming_languages, List<String> databases, List<String> prof_interess, boolean blacklisted, List<Student> Students){
        return searchcontroller.filterstudents(query, programming_languages, databases, prof_interess, blacklisted, Students);
    }

    public void goToHomePage(ActionEvent event) throws IOException, IOException {
        Parent homePageRoot = FXMLLoader.load(getClass().getResource("home-page.fxml"));
        Scene homePageScene = new Scene(homePageRoot);

        // Get current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(homePageScene);
        stage.show();
    }

    public List<String> getProgrammingLanguages(){
        return PLController.getAllLanguages();
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
