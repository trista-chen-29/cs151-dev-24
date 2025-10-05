package cs151.application.ProgrammingLanguages;

import javafx.fxml.FXML;
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

    public PLController(){
    }

    @FXML
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
    }
    @FXML
    public void onGoHome(ActionEvent e){


    }

    private boolean validator(String name){
        return name.trim().isEmpty();
    }


}
