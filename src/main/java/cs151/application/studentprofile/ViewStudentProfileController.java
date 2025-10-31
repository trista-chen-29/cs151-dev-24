package cs151.application.studentprofile;

import cs151.application.homepage.HomePageController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

import cs151.application.AppState;

public class ViewStudentProfileController {

    // Header
    @FXML private TextField tfSearch;
    @FXML private ToggleGroup tgShow;
    @FXML private ToggleButton tbAll, tbWhitelist, tbBlacklist;

    // Table
    @FXML private TableView<StudentRow> tvStudents;
    @FXML private TableColumn<StudentRow,String> colName, colStatus, colRole, colLanguages, colDatabases;
    @FXML private TableColumn<StudentRow,String> colEmployed, colWL, colBL;
    @FXML private TableColumn<StudentRow,String> colJob;

    // Detail
    @FXML private Label lbName, lbStatus, lbEmployment, lbJob, lbRole;
    @FXML private Label badgeBlacklisted, badgeWhitelist;
    @FXML private FlowPane fpLangs, fpDbs;
    @FXML private ListView<String> lvComments;
    @FXML private TextField tfNewComment;

    private Long selectedStudentId = null;
    private final ViewStudentProfileService svc = new ViewStudentProfileService();

    @FXML
    private void initialize() {
        // Table columns
        colName.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getName())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getAcademicStatus())));
        colEmployed.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEmployed() ? "✓" : "—"));
        colJob.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEmployed() ? nullToEmpty(c.getValue().getJobDetails()) : "—"));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getPreferredRole())));
        colLanguages.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getLanguages())));
        colDatabases.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getDatabases())));
        colWL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isWhitelist() ? "YES" : "—"));
        colBL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isBlacklisted() ? "YES" : "—"));

        // Center WL/BL/EMPL text
        center(colEmployed); center(colWL); center(colBL);

        // Selection listener
        tvStudents.getSelectionModel().selectedItemProperty().addListener((obs, old, row) -> {
            showDetails(row);
            selectedStudentId = row == null ? null : row.getId();
        });

        if (tbAll != null) { // set mode first so refresh uses it
            switch (AppState.directoryMode) {
                case WL -> tbWhitelist.setSelected(true);
                case BL -> tbBlacklist.setSelected(true);
                default -> tbAll.setSelected(true);
            }
        }

        if (tfSearch != null && AppState.directoryQuery != null && !AppState.directoryQuery.isBlank()) {
            tfSearch.setText(AppState.directoryQuery);
        }

        tfNewComment.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER && (e.isControlDown() || e.isMetaDown())) {
                onPostComment();
            }
        });

        // trigger first load
        refreshTable();
        // clear for the next navigation
        AppState.clearDirectorySearch();
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private void center(TableColumn<?,?> col) { col.setStyle("-fx-alignment: CENTER;"); }

    /* ---------- Data ---------- */

    private ViewStudentProfileService.FilterMode mode() {
        if (tbWhitelist != null && tbWhitelist.isSelected()) return ViewStudentProfileService.FilterMode.WL;
        if (tbBlacklist != null && tbBlacklist.isSelected()) return ViewStudentProfileService.FilterMode.BL;
        return ViewStudentProfileService.FilterMode.ALL;
    }

    private void refreshTable() {
        // remember current selection
        final Long keepId = selectedStudentId;

        var filtered = svc.find(
                tfSearch != null ? tfSearch.getText() : "",
                mode()
        );

        tvStudents.setItems(FXCollections.observableArrayList(filtered));

        if (keepId != null) {
            tvStudents.getItems().stream()
                    .filter(r -> java.util.Objects.equals(r.getId(), keepId))
                    .findFirst()
                    .ifPresent(r -> tvStudents.getSelectionModel().select(r));
        } else if (!tvStudents.getItems().isEmpty()) {
            tvStudents.getSelectionModel().selectFirst();
        } else {
            clearDetails();
        }
    }

    private void showDetails(StudentRow row) {
        if (row == null) { clearDetails(); return; }

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

        // ALL comments (newest first)
        lvComments.setItems(FXCollections.observableArrayList(
                svc.listComments(row.getId()).stream()
                        .map(c -> (c.getCreatedAt() == null ? "" : c.getCreatedAt() + " — ") + c.getBody())
                        .toList()
        ));
    }

    private void clearDetails() {
        lbName.setText("Select a student");
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

    @FXML private void onBackHome() { HomePageController.goHomeFrom((Node) tvStudents); }


    @FXML
    public void onDelete() {
        if (selectedStudentId == null) return;
        if (svc.deleteStudent(selectedStudentId)) {
            selectedStudentId = null;
            refreshTable();
        } else {
            new Alert(Alert.AlertType.ERROR, "Delete failed.").showAndWait();
        }
    }

    @FXML
    private void onRefresh() { refreshTable(); }

    @FXML
    private void onPostComment() {
        StudentRow row = tvStudents.getSelectionModel().getSelectedItem();
        String body = tfNewComment.getText() == null ? "" : tfNewComment.getText().trim();
        if (row == null || body.isEmpty()) return;
        if (svc.addComment(row.getId(), body)) {
            tfNewComment.clear();
            refreshTable();
            tvStudents.getItems().stream()
                    .filter(r -> r.getId() == row.getId())
                    .findFirst().ifPresent(this::showDetails);
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to post comment.").showAndWait();
        }
    }

    @FXML
    private void onEditStudent(ActionEvent e) {
        var row = tvStudents.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a student to edit.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/cs151/application/define-student-profile.fxml"));
            Parent root = loader.load();

            DefineStudentProfileController ctrl = loader.getController();
            ctrl.loadForEdit(row.getId());

            // switch scene
            ((Node) e.getSource()).getScene().setRoot(root);
        } catch (IOException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Cannot open Student Profile:\n" + ex.getMessage());
            a.setHeaderText("Load failed");
            a.showAndWait();
            ex.printStackTrace();
        }
    }

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