package cs151.application.reports;

import cs151.application.persistence.StudentReportDAO;
import cs151.application.studentprofile.StudentRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import java.util.List;

public class ViewStudentReportController {

    // Detail
    @FXML private Label lbName, lbStatus, lbEmployment, lbJob, lbRole;
    @FXML private Label badgeBlacklisted, badgeWhitelist;
    @FXML private FlowPane fpLangs, fpDbs;
    @FXML private ListView<Label> lvComments;

    private Long studentId = null;
    private final StudentReportDAO dao = new StudentReportDAO();

    @FXML
    private void initialize() {
        // Auto-adjust Label width for wrapping
        lvComments.widthProperty().addListener((obs, oldW, newW) -> {
            double max = newW.doubleValue() - 20;
            for (Label lbl : lvComments.getItems()) {
                lbl.setMaxWidth(max);
            }
        });

        // Click listener to open comment viewer
        lvComments.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                Label selectedLabel = lvComments.getSelectionModel().getSelectedItem();
                if (selectedLabel != null) {
                    String[] parts = selectedLabel.getText().split("—", 2);
                    if (parts.length == 2) {
                        String date = parts[0].trim();
                        String text = parts[1].trim();
                        ReportNav.openCommentViewer(lvComments.getScene().getRoot(), date, text);
                    }
                }
            }
        });
    }

    /**
     * Load student data by ID. Called from navigation.
     */
    public void loadStudent(long id, String name) {
        this.studentId = id;

        // Find the student in the DAO
        List<StudentRow> all = dao.findAllStudents("");
        StudentRow student = all.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);

        if (student == null) {
            clearDetails();
            return;
        }

        showDetails(student);
    }

    private void showDetails(StudentRow row) {
        if (row == null) {
            clearDetails();
            return;
        }

        lbName.setText(row.getName());
        lbStatus.setText(row.getAcademicStatus() == null ? "—" : row.getAcademicStatus());
        lbEmployment.setText(row.isEmployed() ? "Employed" : "Not employed");
        lbJob.setText(row.isEmployed() ? nullToEmpty(row.getJobDetails()) : "—");
        lbRole.setText(row.getPreferredRole() == null ? "—" : row.getPreferredRole());

        boolean bl = row.isBlacklisted();
        boolean wl = row.isWhitelist();
        badgeBlacklisted.setVisible(bl); badgeBlacklisted.setManaged(bl);
        badgeWhitelist.setVisible(wl);   badgeWhitelist.setManaged(wl);

        // Chips
        fpLangs.getChildren().setAll(Tags.make(row.getLanguages()));
        fpDbs.getChildren().setAll(Tags.make(row.getDatabases()));

        // Build wrapped Labels for comments
        var comments = dao.getCommentsForStudent(row.getId());
        var labels = comments.stream()
                .map(c -> {
                    String ts = c.getCreatedAt();
                    String dateOnly = (ts == null || ts.isBlank()) ? "" : ts.split(" ")[0];
                    Label lbl = new Label(dateOnly + " — " + c.getBody());
                    lbl.setWrapText(true);
                    lbl.setMaxWidth(lvComments.getWidth() - 20);
                    return lbl;
                })
                .toList();

        lvComments.setItems(FXCollections.observableArrayList(labels));
    }

    private void clearDetails() {
        lbName.setText("No student selected");
        lbStatus.setText("");
        lbEmployment.setText("");
        lbJob.setText("");
        lbRole.setText("");
        badgeBlacklisted.setVisible(false); badgeBlacklisted.setManaged(false);
        badgeWhitelist.setVisible(false);   badgeWhitelist.setManaged(false);
        fpLangs.getChildren().clear();
        fpDbs.getChildren().clear();
        lvComments.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void onBackToReports() {
        ReportNav.backToReports((Node) lbName);
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }

    static class Tags {
        static java.util.List<Label> make(String csv) {
            java.util.List<Label> out = new java.util.ArrayList<>();
            if (csv == null || csv.isBlank()) return out;
            for (String t : csv.split(",")) {
                String text = t.trim();
                if (text.isEmpty()) continue;
                Label lbl = new Label(text);
                lbl.getStyleClass().add("chip");
                out.add(lbl);
            }
            return out;
        }
    }
}