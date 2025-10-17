package cs151.application.studentprofile;

import cs151.application.homepage.HomePageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class StudentProfileController {

    @FXML private TextField tfFullName;
    @FXML private ComboBox<String> cbAcademicStatus;
    @FXML private RadioButton rbEmployed, rbUnemployed;
    @FXML private TextArea taJobDetails;

    @FXML private ListView<String> lvProgLangs;
    @FXML private ListView<String> lvDatabases;

    @FXML private ComboBox<String> cbRole;

    @FXML private TextArea taNewComment;
    @FXML private ListView<String> lvComments;

    @FXML private CheckBox cbWhitelist, cbBlacklist;
    @FXML private Label lbError;

    @FXML private ImageView ivPhoto;

    @FXML
    private void initialize() {
        // Static lists to exactly match your screenshots
        cbAcademicStatus.getItems().setAll("Freshman", "Sophomore", "Junior", "Senior", "Graduate");

        lvProgLangs.getItems().setAll("C++", "Java", "JavaScript", "SQL", "Python", "C#");
        lvDatabases.getItems().setAll("MongoDB", "SQL Server", "Oracle", "PostgreSQL", "SQLite");

        cbRole.getItems().setAll(
                "Data Scientist", "Data Analyst", "AI Engineer",
                "Software Engineer", "Cybersecurity Analyst", "Backend Developer"
        );

        // Job details enabled only when Employed
        rbEmployed.selectedProperty().addListener((o, ov, nv) -> taJobDetails.setDisable(!nv));
        taJobDetails.setDisable(true); // default disabled
    }

    @FXML
    private void onGoBackHome(ActionEvent e) {
        // Reuse existing shared helper (keeps Scene & CSS)
        HomePageController.goHomeFrom((Button) e.getSource());
    }

    @FXML
    private void onUpdatePhoto(ActionEvent e) {
        new Alert(Alert.AlertType.INFORMATION, "Update photo will be implemented later.").showAndWait();
    }

    @FXML
    private void onAddComment(ActionEvent e) {
        String text = taNewComment.getText();
        if (text != null && !text.trim().isEmpty()) {
            lvComments.getItems().add(text.trim());
            taNewComment.clear();
        }
    }

    @FXML
    private void onClear(ActionEvent e) {
        tfFullName.clear();
        cbAcademicStatus.getSelectionModel().clearSelection();
        rbEmployed.setSelected(false);
        rbUnemployed.setSelected(false);
        taJobDetails.clear();
        taJobDetails.setDisable(true);

        lvProgLangs.getSelectionModel().clearSelection();
        lvDatabases.getSelectionModel().clearSelection();

        cbRole.getSelectionModel().clearSelection();
        taNewComment.clear();
        lvComments.getItems().clear();

        cbWhitelist.setSelected(false);
        cbBlacklist.setSelected(false);

        lbError.setText("");
    }

    @FXML
    private void onSave(ActionEvent e) {
        new Alert(Alert.AlertType.INFORMATION, "Save will be implemented by the controller/service owners.").showAndWait();
    }
}
