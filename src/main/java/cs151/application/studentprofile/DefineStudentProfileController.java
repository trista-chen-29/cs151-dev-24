package cs151.application.studentprofile;

import cs151.application.homepage.HomePageController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import cs151.application.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefineStudentProfileController {

    @FXML private TextField tfFullName;
    @FXML private ComboBox<String> cbAcademicStatus;
    @FXML private RadioButton rbEmployed, rbUnemployed;
    @FXML private ToggleGroup tgJob;
    @FXML private TextArea taJobDetails;

    @FXML private ListView<String> lvProgLangs;
    @FXML private ListView<String> lvDatabases;

    @FXML private ComboBox<String> cbRole;

    @FXML private TextArea taNewComment;
    @FXML private ListView<String> lvComments;

    @FXML private CheckBox cbWhitelist, cbBlacklist;
    @FXML private Label lbError;

    @FXML private ImageView ivPhoto;

    @FXML private Button btnSave;
    @FXML private Label  titleLabel;

    private final Map<String, BooleanProperty> progLangState = new HashMap<>();
    private final Map<String, BooleanProperty> Dbstate = new HashMap<>();
    private final ProgrammingLanguagesDAO languages = new ProgrammingLanguagesDAO();

    private long editingId = -1;
    private final DefineStudentProfileService service = new DefineStudentProfileService();

    @FXML
    private void initialize() {
        ObservableList<String> langs = FXCollections.observableArrayList(languages.listAll());
        lvProgLangs.setItems(langs);
        lvProgLangs.setCellFactory(lv -> new CheckBoxListCell<>(item -> {
            return progLangState.computeIfAbsent(item, k -> new SimpleBooleanProperty(false));
        }));

        lvDatabases.getItems().setAll("MySQL","Postgres","MongoDB");
        lvDatabases.setCellFactory(lv -> new CheckBoxListCell<>(item -> {
            return Dbstate.computeIfAbsent(item, k -> new SimpleBooleanProperty(false));
        }));

        // Displays all DBs,preferred professional roles, and academic Status from problem statement.
        // it would be best to persist these in SQLite on start up and call a listALL() method like in ProgrammingLanguagesDAO.
        cbRole.getItems().setAll("Front-End","Back-End","Full-Stack","Data");
        cbAcademicStatus.getItems().setAll("Freshman", "Sophomore", "Junior", "Senior", "Graduate");

        cbWhitelist.selectedProperty().addListener((o,ov,nv) -> {if(nv) cbBlacklist.setSelected(false);});
        cbBlacklist.selectedProperty().addListener((o,ov,nv) -> {if(nv) cbWhitelist.setSelected(false);});
        // Job details enabled only when Employed
        rbEmployed.selectedProperty().addListener((o, ov, nv) -> taJobDetails.setDisable(!nv));

        taJobDetails.setDisable(!rbEmployed.isSelected());
    }

    @FXML
    private void onGoBackHome(ActionEvent e) {
        // Reuse existing shared helper (keeps Scene & CSS)
        HomePageController.goHomeFrom((Button) e.getSource());
    }

    @FXML
    private void onUpdatePhoto(ActionEvent e) {
        new Alert(Alert.AlertType.INFORMATION, "Update photo will be implemented later.").showAndWait();
    }

    @FXML
    private void onAddComment(ActionEvent e) {
        String text = taNewComment.getText();
        if (text != null && !text.trim().isEmpty()) {
            lvComments.getItems().add(text.trim());
            taNewComment.clear();
        }
    }

    @FXML
    private void onClear(ActionEvent e) {
        tfFullName.clear();
        cbAcademicStatus.getSelectionModel().clearSelection();
        rbEmployed.setSelected(false);
        rbUnemployed.setSelected(false);
        tgJob.selectToggle(null);
        taJobDetails.setDisable(true);

        lvDatabases.getSelectionModel().clearSelection();

        progLangState.values().forEach(p -> p.set(false));
        Dbstate.values().forEach(p -> p.set(false));

        cbRole.getSelectionModel().clearSelection();
        taNewComment.clear();
        lvComments.getItems().clear();

        cbWhitelist.setSelected(false);
        cbBlacklist.setSelected(false);

        lbError.setText("");
    }

    @FXML
    private void onSave(ActionEvent e) {
        lbError.setText("");

        Student inStudent = compileInput();
        DefineStudentProfileService.Result r = (editingId > 0)
                ? service.update(editingId, inStudent)
                : service.create(inStudent);

        if (!r.ok) {
            setError(r.message);
            return;
        }

        lbError.setTextFill(Color.GREEN);
        lbError.setText(editingId > 0 ? "Changes saved!" : "Student saved!");
    }

    public void loadForEdit(long id) {
        this.editingId = id;

        // pull full student
        Student s = service.findById(id);
        if (s == null) {
            new Alert(Alert.AlertType.ERROR, "Student not found (id=" + id + ")").showAndWait();
            return;
        }

        // Populate the form controls
        tfFullName.setText(s.getName());
        cbAcademicStatus.getSelectionModel().select(s.getAcademicStatus());

        // select the correct radio and toggle job details
        if (tgJob != null) tgJob.selectToggle(s.getEmploymentStatus() ? rbEmployed : rbUnemployed);
        taJobDetails.setDisable(!s.getEmploymentStatus());
        taJobDetails.setText(s.getEmploymentStatus() ? (s.getJobDetails() == null ? "" : s.getJobDetails()) : "");

        cbRole.getSelectionModel().select(s.getProfessionalRole());
        cbWhitelist.setSelected(s.getWhiteList());
        cbBlacklist.setSelected(s.getBlackList());

        setLangCheckboxesFrom(s.getLanguages());
        setDbCheckboxesFrom(s.getStudentDbs());

        if (titleLabel != null) titleLabel.setText("Edit: " + s.getName());
        if (btnSave != null)    btnSave.setText("Save changes");

        // (Optional) show existing comments if load them with findById
        if (s.getComments() != null && !s.getComments().isEmpty()) {
            lvComments.getItems().setAll(s.getComments());
        }
    }

    // HELPERS

    /** returns a List of checked items */
    private static List<String> getCheckedItemsList(Map<String,BooleanProperty> map){
        List<String> checked = new ArrayList<>();
        map.forEach((k,p) -> {if(p.get()) checked.add(k);});
        return checked;
    }

    /** Creates a student object for validator */
    private Student compileInput(){
       Student in = new Student(
               tfFullName.getText(),
               cbRole.getValue(),
               cbAcademicStatus.getValue(),
               taJobDetails.getText(),
               cbBlacklist.selectedProperty().get(),
               cbWhitelist.selectedProperty().get(),
               rbEmployed.selectedProperty().get(),
               getCheckedItemsList(progLangState),
               getCheckedItemsList(Dbstate),
               new ArrayList<>(lvComments.getItems())
        );

        return in;
    }

    private void setDbCheckboxesFrom(List<String> dbs) {
        var want = (dbs == null) ? java.util.Set.<String>of()
                : dbs.stream()
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());

        for (String name : lvDatabases.getItems()) {
            BooleanProperty p = Dbstate.computeIfAbsent(name, k -> new SimpleBooleanProperty(false));
            p.set(want.contains(name.toLowerCase()));
        }
    }

    private void setLangCheckboxesFrom(List<String> langs) {
        var want = (langs == null) ? java.util.Set.<String>of()
                : langs.stream()
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(java.util.stream.Collectors.toSet());

        // ensure properties exist and tick them
        for (String name : lvProgLangs.getItems()) {
            BooleanProperty p = progLangState.computeIfAbsent(name, k -> new SimpleBooleanProperty(false));
            p.set(want.contains(name.toLowerCase()));
        }
    }

    //Testing Method to see if Student is stored properly.
    private void printStudent(Student s){
       System.out.println( "Student{" +
                "fullName='" + s.getName() + "'\n" +
                " academicStatus='" + s.getAcademicStatus() + "'\n" +
                " employed=" + s.getEmploymentStatus() +"\n" +
                " jobDetails='" + s.getJobDetails()  + "'\n" +
                " preferredRole='" + s.getProfessionalRole() + "'\n" +
                " whitelist=" + s.getWhiteList() +"\n"+
                " blacklist=" + s.getBlackList() +"\n"+
                " languages=" + s.getLanguages() +"\n"+
                " databases=" + s.getStudentDbs() +"\n"+
                " comments=" + s.getComments() + "\n"+
                '}');
    }

    private void setError(String error){
        lbError.setText(error);
        lbError.setTextFill(Color.RED);
    }
}
