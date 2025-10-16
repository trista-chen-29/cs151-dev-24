package cs151.application.homepage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomePageController {

    // ---------- Navigation from Home ----------

    @FXML
    private void onGoToLanguages(ActionEvent event) {
        String[] candidates = new String[] {
                "/cs151/application/define-pl.fxml",
                "/cs151/application/programminglanguages/define-pl.fxml"
        };
        switchScene(event, candidates, "Cannot open Programming Languages");
    }

    @FXML
    private void onGoToStudentProfile(ActionEvent event) {
        String[] candidates = new String[] {
                "/cs151/application/studentprofile.fxml",
                "/cs151/application/studentprofile/studentprofile.fxml"
        };
        switchScene(event, candidates, "Cannot open Student Profile");
    }

    // ---------- Optional handlers used by homepage.fxml ----------

    @FXML
    private void onSearch(ActionEvent event) {
        // TODO implement search later
    }

    @FXML
    private void onApplyFilters(ActionEvent event) {
        // TODO implement filters later
    }

    // ---------- Shared “Go Home” helpers (used by other controllers) ----------

    /** Allow any controller to return to Home using a Node (e.g., Button). */
    public static void goHomeFrom(Node source) {
        switchTo("/cs151/application/homepage.fxml", source);
    }

    /** Overload for convenience — matches goHomeFrom(Button) calls. */
    public static void goHomeFrom(Button source) {
        goHomeFrom((Node) source);
    }

    // ---------- Internal utilities ----------

    private static void switchTo(String resource, Node source) {
        try {
            Parent root = FXMLLoader.load(HomePageController.class.getResource(resource));
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Cannot open Home");
            a.setContentText(ex.getMessage());
            a.showAndWait();
            ex.printStackTrace();
        }
    }

    private void switchScene(ActionEvent event, String[] resourceCandidates, String errorHeader) {
        Exception lastError = null;
        for (String res : resourceCandidates) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource(res));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
                return;
            } catch (Exception ex) {
                lastError = ex;
            }
        }
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(errorHeader);
        a.setContentText(lastError != null ? lastError.getMessage() : "Unknown error");
        a.showAndWait();
        if (lastError != null) {
            lastError.printStackTrace();
        }
    }
}
