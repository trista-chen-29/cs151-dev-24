package cs151.application.ProgrammingLanguages;

import javafx.fxml.FXML;
<<<<<<< HEAD
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class PLController {
    // DAO class instance variable would be created here.
    //private final ProgrammingLanguagesDAO repo = new ProgrammingLanguagesDAO();
    @FXML private TextField languageField;
    @FXML private Label errorLabel;
    @FXML private ListView<String> languageList;
=======
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
>>>>>>> 12b5da8 (ui: fix .fxml; define-pl&home-page: fix Controller; persist: build SQLite service)

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PLController {
    // --- UI elements from define-pl.fxml ---
    @FXML private TextField languageField;
    @FXML private Label errorLabel;
    @FXML private ListView<String> languageList;

    // --- Data access ---
    private final ProgrammingLanguagesDAO repo = new ProgrammingLanguagesDAO();

    public PLController() {}

    // Called automatically after FXML loads
    @FXML
    private void initialize() {
        if (languageList != null) {
            languageList.getItems().setAll(repo.listAll());
        }
    }

    @FXML
<<<<<<< HEAD
    public void onSave(ActionEvent e){
        String name = languageField.getText().trim();

        //Check if name is just spaces or is empty
        if(validator(name)){
            errorLabel.setText("Name is required");
            return;
        }

        //check if repo.existsByName(name) sends T or F. checks if it's in languageList for now.
         if(languageList.getItems().contains(name)){
            errorLabel.setText("Name already exists");
            return;
         }

         //try to persist the name
         try{
             //repo.persist(name);
             Language newLanguage = new Language(name.trim().toLowerCase());

             //Send the language 'name' to 'languageList' (temporary solution)
             languageList.getItems().add(name);
             errorLabel.setText("Saved '" + name + "' successfully");

         }catch(Exception ex){
             errorLabel.setText("Couldn't save. Try Again");

         }
=======
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
        if (languageList != null)  languageList.getItems().setAll(repo.listAll());
>>>>>>> 12b5da8 (ui: fix .fxml; define-pl&home-page: fix Controller; persist: build SQLite service)
    }
    @FXML
    public void onGoHome(ActionEvent e){


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

<<<<<<< HEAD

=======
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
>>>>>>> 12b5da8 (ui: fix .fxml; define-pl&home-page: fix Controller; persist: build SQLite service)
}
