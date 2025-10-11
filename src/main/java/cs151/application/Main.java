package cs151.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Temporary: clear all data in language_catalog
        new cs151.application.ProgrammingLanguages.ProgrammingLanguagesDAO().deleteAll();

        // Load home page
        FXMLLoader fxml = new FXMLLoader(
                getClass().getResource("/cs151/application/homepage.fxml")
        );

        Scene scene = new Scene(fxml.load(), 980, 620);

        // Apply theme
        scene.getStylesheets().add(
                getClass().getResource("/cs151/application/theme.css").toExternalForm()
        );

        stage.setTitle("Prof-Support");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
