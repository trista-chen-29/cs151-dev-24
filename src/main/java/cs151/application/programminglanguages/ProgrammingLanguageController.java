package cs151.application.programminglanguages;

import cs151.application.persistence.ProgrammingLanguagesDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProgrammingLanguageController {
    // --- UI elements from define-programming-language.fxml ---
    @FXML private TextField languageField;
    @FXML private Label errorLabel;
    @FXML private TableView<Language> languagesTable;
    @FXML private TableColumn<Language, String> nameColumn;

    // --- Data access ---
    private final ProgrammingLanguagesDAO repo = new ProgrammingLanguagesDAO();

    public ProgrammingLanguageController() {}

    // Called automatically after FXML loads
    @FXML
    private void initialize() {
        if (languagesTable != null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
            languagesTable.getItems().setAll(repo.listLanguageObjects());
            // show sort arrow A→Z
            nameColumn.setSortType(TableColumn.SortType.ASCENDING);
            languagesTable.getSortOrder().setAll(nameColumn);
            languagesTable.sort();
        }
    }

    @FXML
    private void onGoHome(ActionEvent e) throws Exception {
        // Load the homepage FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs151/application/homepage.fxml"));
        Parent root = loader.load();

        // swap the root in the existing Scene (keeps current stylesheets)
        Scene scene = ((Node) e.getSource()).getScene();
        scene.setRoot(root);
    }

    // Called by: <Button onAction="#onSave" .../>
    @FXML
    private void onSave() {
        String name = (languageField != null && languageField.getText() != null)
                ? languageField.getText().trim()
                : "";

        if (name.isEmpty()) {
            setError("Name is required");
            return;
        }

        boolean inserted = repo.save(name);   // <-- persist via DAO
        if (!inserted) {
            setError("Name already exists");
            return;
        }

        // success
        clearError();
        if (languageField != null) languageField.clear();
        if (languagesTable != null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("languageName"));
            languagesTable.getItems().setAll(repo.listLanguageObjects());
        }
    }

    // Now it delegates to the DAO so callers still work.
    public String saveLanguageName(String name){
        if (name == null || name.trim().isEmpty()) {
            return showError("Name is required");
        }
        String trimmed = name.trim();

        boolean inserted = repo.save(trimmed);
        if (!inserted) {
            return showError("Name Already Exists");
        }
        // (You had lowercasing before; keep display as typed)
        return "Saved " + trimmed + " successfully";
    }

    // helpers
    private void setError(String msg) {
        if (errorLabel != null)
            errorLabel.setText(msg);
    }
    private void clearError() {
        if (errorLabel != null)
            errorLabel.setText("");
    }
    private String showError(String e){
        return "ERROR: " + e;
    }
}
