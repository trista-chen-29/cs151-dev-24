package cs151.application.studentprofile;

import cs151.application.AppState;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import java.util.Objects;

public class CommentsController {

    // Form
    @FXML private TextArea taBody;
    @FXML private Label lbError;          // red error text (optional)
    @FXML private Label lbTitle;          // e.g., "Comments — Alice Chen" (optional)

    // List
    @FXML private TableView<CommentVM> tvComments;
    @FXML private TableColumn<CommentVM,String> colWhen, colBody;

    private long studentId = -1;
    private final CommentsService comments = new CommentsService();

    @FXML
    private void initialize() {
        // table bindings
        colWhen.setCellValueFactory(c -> {
            String ts = c.getValue().createdAt;
            String dateOnly = (ts == null || ts.isBlank()) ? "" : ts.split(" ")[0];
            return new SimpleStringProperty(dateOnly);
        });

        colBody.setCellValueFactory(c ->
                new SimpleStringProperty(Objects.toString(c.getValue().body, "")));

        // Cmd/Ctrl + Enter = Save
        taBody.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> {
                    if (e.isMetaDown() || e.isControlDown()) onAdd();
                }
            }
        });
    }

    /** Called by the list page after loading FXML. */
    public void loadStudent(long id, String name) {
        this.studentId = id;
        if (lbTitle != null) lbTitle.setText("Comments — " + name);
        refresh();
    }

    /* ---------- Buttons ---------- */

    @FXML
    private void onAdd() {
        clearError();
        String body = taBody.getText() == null ? "" : taBody.getText().trim();
        if (body.isEmpty()) { error("Type a comment before saving."); return; }

        boolean ok = comments.addComment(studentId, body);  // use your service’s add method name
        if (!ok) { error("Could not save comment."); return; }

        taBody.clear();
        refresh();
    }

    @FXML
    private void onBackToProfile(javafx.event.ActionEvent e) {
        try {
            // tell the next screen which student to reselect
            AppState.preselectStudent(studentId);

            var fx = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/cs151/application/view-student-profile.fxml"));
            javafx.scene.Parent root = fx.load();
            ((javafx.scene.Node) e.getSource()).getScene().setRoot(root);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to open profile:\n" + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }
    }

    /* ---------- Helpers ---------- */

    private void refresh() {
        var rows = comments.listComments(studentId).stream()
                .map((cs151.application.studentprofile.Comment c) ->
                        new CommentVM(
                                c.getId(),
                                c.getCreatedAt() == null ? "" : c.getCreatedAt(),
                                c.getBody()))
                .toList();

        tvComments.setItems(FXCollections.observableList(new java.util.ArrayList<>(rows)));
    }

    private void error(String m) { if (lbError != null) lbError.setText(m); }
    private void clearError()    { if (lbError != null) lbError.setText(""); }

    /** Minimal VM for the table. */
    public static class CommentVM {
        public final long id;
        public final String createdAt;
        public final String body;
        public CommentVM(long id, String createdAt, String body) {
            this.id = id; this.createdAt = createdAt; this.body = body;
        }
    }
}