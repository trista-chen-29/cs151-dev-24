package cs151.application.ProgrammingLanguages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Comparator;

public class PLController {

    @FXML private TextField languageField;
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private ListView<String> languageList;
    @FXML private Label errorLabel;

    private final ProgrammingLanguagesDAO dao = new ProgrammingLanguagesDAO();
    private final ObservableList<String> languages = FXCollections.observableArrayList();

    // same comparator as DAO to keep UI ordering identical
    private static final Comparator<String> LOWER_FIRST = (a, b) -> {
        int ci = a.compareToIgnoreCase(b);
        if (ci != 0) return ci;
        int n = Math.min(a.length(), b.length());
        for (int i = 0; i < n; i++) {
            char ca = a.charAt(i), cb = b.charAt(i);
            if (Character.toLowerCase(ca) != Character.toLowerCase(cb)) break;
            if (ca != cb) {
                if (Character.isLowerCase(ca) && Character.isUpperCase(cb)) return -1;
                if (Character.isUpperCase(ca) && Character.isLowerCase(cb)) return 1;
            }
        }
        return a.compareTo(b);
    };

    @FXML
    public void initialize() {
        // show as a sorted view (a..z before A..Z)
        languageList.setItems(languages.sorted(LOWER_FIRST));
        refreshList();

        if (saveButton != null) saveButton.setOnAction(e -> handleSave());
        if (clearButton != null) clearButton.setOnAction(e -> handleClear());

        if (languageField != null) {
            languageField.textProperty().addListener((obs, o, n) -> showError(""));
        }
    }

    private void handleSave() {
        String name = languageField.getText() == null ? "" : languageField.getText().trim();
        if (name.isEmpty()) { showError("Please enter a language name."); return; }

        // block duplicates (case-insensitive) and show warning dialog like your screenshot
        if (dao.existsIgnoreCase(name) || !dao.insert(name)) {
            showError("Language already exists.");
            Alert alert = new Alert(Alert.AlertType.WARNING, "Cannot add duplicate value.", ButtonType.OK);
            alert.setHeaderText("Warning");
            alert.showAndWait();
            return;
        }

        showError("");
        languageField.clear();
        refreshList();
    }

    private void handleClear() {
        dao.clearAll();      // temporary clear for your testing
        showError("");
        refreshList();
    }

    private void refreshList() {
        languages.setAll(dao.getAll());   // already lower-first ordered
    }

    private void showError(String msg) {
        if (errorLabel != null) errorLabel.setText(msg == null ? "" : msg);
    }
}
