package cs151.application.ProgrammingLanguages;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PLController {

    @FXML private TextField languageField;
    @FXML private Label errorLabel;
    @FXML private ListView<String> languageList;

    private final ProgrammingLanguagesDAO repo = new ProgrammingLanguagesDAO();

    @FXML
    private void initialize() {
        refreshList();
    }

    @FXML
    private void onSave(ActionEvent e) {
        String input = languageField.getText();
        boolean added = repo.save(input);
        if (!added) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Cannot add duplicate value.");
            a.showAndWait();
        }
        languageField.clear();
        refreshList();
    }

    @FXML
    private void onGoHome(ActionEvent e) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs151/application/homepage.fxml"));
        Parent root = loader.load();
        Scene scene = ((Node) e.getSource()).getScene();
        scene.setRoot(root);
    }

    private void refreshList() {
        languageList.getItems().setAll(repo.listAll());
    }
}
