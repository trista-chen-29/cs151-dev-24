package cs151.application.studentprofile;

import cs151.application.homepage.HomePageController;
import cs151.application.persistence.CommentDAO;
import cs151.application.persistence.StudentProfileDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.util.stream.Collectors;

public class ViewStudentProfileController {

    // Header
    @FXML private TextField tfSearch;
    @FXML private ToggleGroup tgShow;
    @FXML private ToggleButton tbAll, tbWhitelist, tbBlacklist;

    // Table
    @FXML private TableView<StudentRow> tvStudents;
    @FXML private TableColumn<StudentRow,String> colName, colStatus, colRole, colLanguages, colDatabases;
    @FXML private TableColumn<StudentRow,String> colEmployed, colWL, colBL;

    // Detail
    @FXML private Label lbName, lbStatus, lbEmployment, lbJob, lbRole;
    @FXML private Label badgeBlacklisted, badgeWhitelist;
    @FXML private FlowPane fpLangs, fpDbs;
    @FXML private ListView<String> lvComments;
    @FXML private TextField tfNewComment;

    private Long selectedStudentId = null;
    private final StudentProfileDAO repo = new StudentProfileDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    @FXML
    private void initialize() {
        // Table columns
        colName.setCellValueFactory(c -> new SimpleStringProperty(nonNull(c.getValue().getName())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(nonNull(c.getValue().getAcademicStatus())));
        colEmployed.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEmployed() ? "✓" : "—"));
        colRole.setCellValueFactory(c -> new SimpleStringProperty(nonNull(c.getValue().getPreferredRole())));
        colLanguages.setCellValueFactory(c -> new SimpleStringProperty(nonNull(c.getValue().getLanguages())));
        colDatabases.setCellValueFactory(c -> new SimpleStringProperty(nonNull(c.getValue().getDatabases())));
        colWL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isWhitelist() ? "YES" : "—"));
        colBL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isBlacklisted() ? "YES" : "—"));

        // Center WL/BL/EMPL text
        center(colEmployed); center(colWL); center(colBL);

        // Selection listener
        tvStudents.getSelectionModel().selectedItemProperty().addListener((obs, old, row) -> {
            showDetails(row);
            selectedStudentId = row == null ? null : row.getId();
        });

        // Load data
        refreshTable();

        // Post on Ctrl/Cmd+Enter
        tfNewComment.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> {
                    if (e.isControlDown() || e.isMetaDown()) onPostComment();
                }
            }
        });

        // re-run filtering whenever user types or flips the toggle
        if (tfSearch != null) {
            tfSearch.textProperty().addListener((o, ov, nv) -> refreshTable());
        }
        if (tgShow != null) {
            tgShow.selectedToggleProperty().addListener((o, ov, nv) -> refreshTable());
        }
        // ensure "All" is selected by default
        if (tbAll != null) tbAll.setSelected(true);
    }

    private void center(TableColumn<?,?> col) {
        col.setStyle("-fx-alignment: CENTER;");
    }

    private static String nonNull(String s) { return s == null ? "" : s; }

    /* ---------- Data ---------- */

    private void refreshTable() {
        var all = repo.listAllForTable();

        final String q = (tfSearch != null && tfSearch.getText() != null)
                ? tfSearch.getText().trim().toLowerCase()
                : "";

        final FilterMode m = mode();

        var filtered = all.stream()
                .filter(r -> {
                    if (m == FilterMode.WL && !r.isWhitelist())   return false;
                    if (m == FilterMode.BL && !r.isBlacklisted()) return false;
                    if (q.isEmpty()) return true;
                    return contains(r.getName(), q)
                            || contains(r.getAcademicStatus(), q)
                            || contains(r.getPreferredRole(), q)
                            || contains(r.getLanguages(), q)
                            || contains(r.getDatabases(), q);
                })
                .collect(java.util.stream.Collectors.toList());

        // keep current selection
        Long tmp = null;
        var sel = tvStudents.getSelectionModel().getSelectedItem();
        if (sel != null) tmp = sel.getId();

        tvStudents.setItems(FXCollections.observableArrayList(filtered));

        final Long selectedId = tmp; // <— final copy for lambda below

        if (selectedId != null) {
            tvStudents.getItems().stream()
                    .filter(r -> r.getId() == selectedId)
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
        lbJob.setText("—"); // Add DAO getter if/when you expose job_details in StudentRow / view
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
                commentDAO.listByStudent(row.getId()).stream()
                        .map(c -> (c.getCreatedAt() == null ? "" : (c.getCreatedAt() + " — ")) + c.getBody())
                        .collect(Collectors.toList())
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

    @FXML
    private void onBackHome() {
        HomePageController.goHomeFrom((Node) tvStudents);
    }

    @FXML
    public void onDelete() {
        if(selectedStudentId == null){return;}

        repo.delete(selectedStudentId);
        refreshTable();
    }

    @FXML
    private void onRefresh() { refreshTable(); }

    @FXML
    private void onPostComment() {
        StudentRow row = tvStudents.getSelectionModel().getSelectedItem();
        String body = tfNewComment.getText() == null ? "" : tfNewComment.getText().trim();
        if (row == null || body.isEmpty()) return;

        if (commentDAO.add(row.getId(), body) > 0) {
            tfNewComment.clear();
            // reload just to refresh last comment / counts
            refreshTable();
            tvStudents.getItems().stream()
                    .filter(r -> r.getId() == row.getId())
                    .findFirst().ifPresent(this::showDetails);
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to post comment.").showAndWait();
        }
    }

    // small helpers
    private static String ns(String s) { return (s == null || s.isBlank()) ? "—" : s; }

    /** tiny tag maker */
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

    private enum FilterMode { ALL, WL, BL }

    private FilterMode mode() {
        if (tbWhitelist != null && tbWhitelist.isSelected()) return FilterMode.WL;
        if (tbBlacklist != null && tbBlacklist.isSelected()) return FilterMode.BL;
        return FilterMode.ALL;
    }

    private static boolean contains(String s, String q) {
        return s != null && s.toLowerCase().contains(q);
    }
}