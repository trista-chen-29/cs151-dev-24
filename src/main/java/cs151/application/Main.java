package cs151.application;

import cs151.application.persistence.DbInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Create tables if you're using SQLite (safe to call repeatedly)
        try {
            DbInit.ensureSchema();
        } catch (Throwable t) {
            // Don't crash the UI if DB init fails during early dev
            t.printStackTrace();
        }
        // Load new home screen (match resource path)
        FXMLLoader fxml = new FXMLLoader(
                getClass().getResource("/cs151/application/homepage.fxml")
        );

        Scene scene = new Scene(fxml.load(), 980, 620);

        // Global theme once at the Scene level
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