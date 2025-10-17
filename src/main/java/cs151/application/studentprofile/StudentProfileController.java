package cs151.application.studentprofile;

import cs151.application.homepage.HomePageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class StudentProfileController {

    @FXML private Button btnGoHome;
    @FXML private ImageView ivPhoto;

    @FXML private TextField tfFullName;
    @FXML private ComboBox<String> cbAcademicStatus;

    @FXML private RadioButton rbEmployed;
    @FXML private RadioButton rbUnemployed;
    @FXML private TextArea taJobDetails;

    @FXML private ListView<String> lvProgLangs;
    @FXML private ListView<String> lvDatabases;

    @FXML private ComboBox<String> cbRole;

    @FXML private TextArea taNewComment;
    @FXML private ListView<String> lvComments;

    @FXML private CheckBox cbWhitelist;
    @FXML private CheckBox cbBlacklist;

    @FXML private Label lbError;

    @FXML
    private void initialize() {
        if (cbAcademicStatus != null) {
            cbAcademicStatus.getItems().setAll(
                    "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
            );
        }
        if (lvProgLangs != null) {
            lvProgLangs.getItems().setAll("C++", "Java", "JavaScript", "SQL", "Python", "C#");
        }
        if (lvDatabases != null) {
            lvDatabases.getItems().setAll("MySQL", "MongoDB", "SQL Server", "Oracle", "PostgreSQL", "SQLite");
        }
        if (cbRole != null) {
            cbRole.getItems().setAll(
                    "Data Scientist",
                    "Data Analyst",
                    "AI Engineer",
                    "Software Engineer",
                    "Cybersecurity Analyst",
                    "Backend Developer",
                    "Frontend Developer",
                    "Full-Stack Developer",
                    "Mobile Developer",
                    "DevOps Engineer"
            );
            cbRole.setEditable(false);
        }
    }

    @FXML
    private void onGoBackHome(ActionEvent e) {
        HomePageController.goHomeFrom(btnGoHome);
    }

    @FXML
    private void onUpdatePhoto(ActionEvent e) {
        showInfo("Photo", "Update photo will be implemented later.");
    }

    @FXML
    private void onAddComment(ActionEvent e) {
        if (taNewComment != null && lvComments != null) {
            String s = safeText(taNewComment.getText());
            if (!s.isEmpty()) {
                lvComments.getItems().add(s);
                taNewComment.clear();
            }
        }
    }

    @FXML
    private void onClear(ActionEvent e) {
        if (tfFullName != null) tfFullName.clear();
        if (cbAcademicStatus != null) cbAcademicStatus.getSelectionModel().clearSelection();
        if (rbEmployed != null) rbEmployed.setSelected(false);
        if (rbUnemployed != null) rbUnemployed.setSelected(false);
        if (taJobDetails != null) taJobDetails.clear();
        if (cbRole != null) cbRole.getSelectionModel().clearSelection();
        if (cbWhitelist != null) cbWhitelist.setSelected(false);
        if (cbBlacklist != null) cbBlacklist.setSelected(false);
        if (lbError != null) lbError.setText("");
        if (lvComments != null) lvComments.getItems().clear();
        if (lvProgLangs != null) lvProgLangs.getSelectionModel().clearSelection();
        if (lvDatabases != null) lvDatabases.getSelectionModel().clearSelection();
    }

    @FXML
    private void onSave(ActionEvent e) {
        showInfo("Save", "Save feature will be implemented later.");
    }

    private String safeText(String s) {
        return s == null ? "" : s.trim();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
