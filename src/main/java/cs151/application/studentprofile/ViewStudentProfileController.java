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

    // Table
    @FXML private TableView<StudentRow> tvStudents;
    @FXML private TableColumn<StudentRow,String> colName, colStatus, colRole, colLanguages, colDatabases;
    @FXML private TableColumn<StudentRow,String> colEmployed;

    // Detail
    @FXML private Label lbName, lbStatus, lbEmployment, lbJob, lbRole;
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

        colEmployed.setStyle("-fx-alignment: CENTER;");

        // Selection listener
        tvStudents.getSelectionModel().selectedItemProperty().addListener((obs, old, row) -> {
            showDetails(row);
            selectedStudentId = row == null ? null : row.getId();
        });

        // Post comment shortcut
        tfNewComment.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> {
                    if (e.isControlDown() || e.isMetaDown()) onPostComment();
                }
            }
        });

        if (tfSearch != null) {
            tfSearch.textProperty().addListener((o, ov, nv) -> refreshTable());
        }
    }


    private static String nonNull(String s) { return s == null ? "" : s; }

    private void refreshTable() {
        var all = repo.listAllForTable();

        final String q = tfSearch.getText() != null ? tfSearch.getText().trim().toLowerCase() : "";

        var filtered = all.stream()
                .filter(r -> q.isEmpty() || contains(r.getName(), q)
                        || contains(r.getAcademicStatus(), q)
                        || contains(r.getPreferredRole(), q)
                        || contains(r.getLanguages(), q)
                        || contains(r.getDatabases(), q))
                .collect(Collectors.toList());

        // Keep current selection
        Long tmp = null;
        var sel = tvStudents.getSelectionModel().getSelectedItem();
        if (sel != null) tmp = sel.getId();

        tvStudents.setItems(FXCollections.observableArrayList(filtered));

        final Long selectedId = tmp;

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
        lbJob.setText("—");
        lbRole.setText(row.getPreferredRole() == null ? "—" : row.getPreferredRole());

        fpLangs.getChildren().setAll(Tags.make(row.getLanguages()));
        fpDbs.getChildren().setAll(Tags.make(row.getDatabases()));

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
        if(selectedStudentId == null) return;

        repo.delete(selectedStudentId);
        refreshTable();
    }

    public void updateSearchQuery(String query) {
        if (tfSearch != null) {
            tfSearch.setText(query);
            refreshTable(); // immediately filter the table
        }
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
            refreshTable();
            tvStudents.getItems().stream()
                    .filter(r -> r.getId() == row.getId())
                    .findFirst().ifPresent(this::showDetails);
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to post comment.").showAndWait();
        }
    }

    // Helper
    private static boolean contains(String s, String q) {
        return s != null && s.toLowerCase().contains(q);
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