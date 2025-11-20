package cs151.application.reports;

import cs151.application.AppState;
import cs151.application.homepage.HomePageController;
import cs151.application.studentprofile.StudentRow;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.Node;

import java.util.List;

public class ReportsController {
    // filter controls
    @FXML private ToggleGroup tgFilter;
    @FXML private ToggleButton tbAll, tbWhitelist, tbBlacklist;

    // table
    @FXML private TableView<StudentRow> tvStudents;
    @FXML private TableColumn<StudentRow, String> colName;

    // misc
    @FXML private Label lbCount;

    private final ReportsService service = new ReportsService();
    private ReportsService.Filter filter = ReportsService.Filter.ALL;

    @FXML
    private void initialize() {
        // table bindings
        colName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName() == null ? "" : c.getValue().getName()));
        tvStudents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Make sure the only column fills the width (prevents the stray vertical line)
        colName.prefWidthProperty().bind(tvStudents.widthProperty().subtract(2));
        tvStudents.getColumns().setAll(colName);

        // double-click to open detail (target left modifiable)
        tvStudents.setRowFactory(tv -> {
            TableRow<StudentRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !row.isEmpty()) {
                    openDetail(row.getItem());
                }
            });
            return row;
        });

        refresh();
    }

    @FXML
    private void onFilterChange() {
        if (tbWhitelist.isSelected())      filter = ReportsService.Filter.WHITELIST;
        else if (tbBlacklist.isSelected()) filter = ReportsService.Filter.BLACKLIST;
        else                               filter = ReportsService.Filter.ALL;
        refresh();
    }

    @FXML
    private void onExportCsv() {
        try {
            var chooser = new javafx.stage.FileChooser();
            chooser.setTitle("Save Student Report (CSV)");
            chooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            chooser.setInitialFileName("student-report.csv");

            var stage = (javafx.stage.Window) tvStudents.getScene().getWindow();
            var file = chooser.showSaveDialog(stage);
            if (file == null) return; // cancelled

            service.writeNamesCsv(tvStudents.getItems(), file.toPath());
            new Alert(Alert.AlertType.INFORMATION, "Exported to:\n" + file.getAbsolutePath()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Export failed:\n" + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }
    }

    @FXML
    private void onBackHome(javafx.event.ActionEvent e) {
        HomePageController.goHomeFrom((Node) e.getSource());
    }

    private void refresh() {
        List<StudentRow> items = service.listNames(filter);
        tvStudents.setItems(FXCollections.observableArrayList(items));
        if (lbCount != null) lbCount.setText("(" + items.size() + " students)");
    }

    /** Navigation NOTE:
     *  When finishing the read-only detail page, just swap the 'target' FXML path.
     */
    private void openDetail(StudentRow item) {
        if (item == null) return;
        AppState.preselectStudent(item.getId());

        // TODO: replace with final read-only page when available
        String target = "/cs151/application/view-student-profile.fxml";

        try {
            var url = getClass().getResource(target);
            if (url == null) throw new IllegalStateException("Detail FXML not found: " + target);
            Parent root = FXMLLoader.load(url);
            tvStudents.getScene().setRoot(root);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Detail page not implemented yet.\n" +
                            "Open ReportsController.openDetail(...) and change the 'target' path when ready.")
                    .showAndWait();
            ex.printStackTrace();
        }
    }
}
