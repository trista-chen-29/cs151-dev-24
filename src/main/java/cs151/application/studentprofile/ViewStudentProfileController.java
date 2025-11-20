package cs151.application.studentprofile;

import cs151.application.AppState;
import cs151.application.homepage.HomePageController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.Locale;

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

    // ↓↓↓ 改成 Label，讓每筆 comment 可以換行
    @FXML private ListView<Label> lvComments;

    private Long selectedStudentId = null;
    private final ViewStudentProfileService svc = new ViewStudentProfileService();
    private final CommentsService commentsSvc = new CommentsService();

    @FXML
    private void initialize() {
        // Table columns
        colName.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getName())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getAcademicStatus())));
        colEmployed.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEmployed() ? "✓" : "—"));
        colJob.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isEmployed()
                        ? nullToEmpty(c.getValue().getJobDetails())
                        : "—"
        ));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getPreferredRole())));
        colLanguages.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getLanguages())));
        colDatabases.setCellValueFactory(c -> new SimpleStringProperty(nullToEmpty(c.getValue().getDatabases())));
        colWL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isWhitelist() ? "YES" : "—"));
        colBL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isBlacklisted() ? "YES" : "—"));

        // Center WL/BL/EMPL text
        center(colEmployed);
        center(colWL);
        center(colBL);

        // Selection listener
        tvStudents.getSelectionModel().selectedItemProperty().addListener((obs, old, row) -> {
            showDetails(row);
            selectedStudentId = (row == null) ? null : row.getId();
        });

        // Restore directory mode from AppState
        if (tbAll != null) {
            switch (AppState.directoryMode) {
                case WL -> tbWhitelist.setSelected(true);
                case BL -> tbBlacklist.setSelected(true);
                default -> tbAll.setSelected(true);
            }
        }

        if (tfSearch != null && AppState.directoryQuery != null && !AppState.directoryQuery.isBlank()) {
            tfSearch.setText(AppState.directoryQuery);
        }

        // 讓 ListView 變寬 / 變窄時，自動調整每個 Label 的最大寬度，保持換行自然
        lvComments.widthProperty().addListener((obs, oldW, newW) -> {
            double max = newW.doubleValue() - 20;
            for (Label lbl : lvComments.getItems()) {
                lbl.setMaxWidth(max);
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

        Long targetId = (selectedStudentId != null)
                ? selectedStudentId
                : AppState.consumePreselectStudent(); // from the comments page

        if (targetId != null) {
            tvStudents.getItems().stream()
                    .filter(r -> java.util.Objects.equals(r.getId(), targetId))
                    .findFirst()
                    .ifPresent(r -> tvStudents.getSelectionModel().select(r));
        } else if (!tvStudents.getItems().isEmpty()) {
            tvStudents.getSelectionModel().selectFirst();
        } else {
            clearDetails();
        }
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

        // Build wrapped Labels for ALL comments (newest first)
        var labels = commentsSvc.listComments(row.getId()).stream()
                .map((cs151.application.studentprofile.Comment c) -> {
                    String ts = c.getCreatedAt();
                    String dateOnly = (ts == null || ts.isBlank()) ? "" : ts.split(" ")[0];
                    Label lbl = new Label(dateOnly + " — " + c.getBody());
                    lbl.setWrapText(true);
                    // Initial max width based on current ListView width
                    lbl.setMaxWidth(lvComments.getWidth() - 20);
                    // Optional style class if you want to customize in CSS
                    // lbl.getStyleClass().add("comment-line");
                    return lbl;
                })
                .toList();

        lvComments.setItems(FXCollections.observableArrayList(labels));
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

    @FXML
    private void onBackHome() {
        HomePageController.goHomeFrom((Node) tvStudents);
    }

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
    private void onRefresh() {
        refreshTable();
    }

    @FXML
    private void onEditStudent(javafx.event.ActionEvent e) {
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

            ((Node) e.getSource()).getScene().setRoot(root);
        } catch (IOException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Cannot open Student Profile:\n" + ex.getMessage());
            a.setHeaderText("Load failed");
            a.showAndWait();
            ex.printStackTrace();
        }
    }

    @FXML
    private void onOpenComments(javafx.event.ActionEvent e) {
        var row = tvStudents.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.INFORMATION, "Select a student first.").showAndWait();
            return;
        }
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/cs151/application/comments.fxml"));
            Parent root = fx.load();

            CommentsController ctrl = fx.getController();
            ctrl.loadStudent(row.getId(), row.getName());

            ((Node) e.getSource()).getScene().setRoot(root);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Cannot open Comments page:\n" + ex.getMessage()).showAndWait();
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
