package cs151.application.studentprofile;

import cs151.application.homepage.HomePageController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.scene.control.cell.CheckBoxListCell;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StudentProfileController {

    @FXML private ImageView ivPhoto;
    @FXML private Button btnUpdatePhoto;

    @FXML private TextField tfFullName;
    @FXML private ComboBox<String> cbAcademicStatus;
    @FXML private ToggleGroup tgJob;
    @FXML private RadioButton rbEmployed;
    @FXML private RadioButton rbUnemployed;
    @FXML private TextArea taJobDetails;

    @FXML private ListView<String> lvProgLangs;
    @FXML private ListView<String> lvDatabases;
    @FXML private TextField tfNewLang;
    @FXML private Button btnAddLang;
    @FXML private TextField tfNewDb;
    @FXML private Button btnAddDb;

    @FXML private ComboBox<String> cbRole;
    @FXML private TextField tfNewRole;
    @FXML private Button btnAddRole;

    @FXML private TextArea taNewComment;
    @FXML private ListView<String> lvComments;
    @FXML private Button btnAddComment;

    @FXML private CheckBox cbWhitelist;
    @FXML private CheckBox cbBlacklist;

    @FXML private Button btnSave;
    @FXML private Button btnClear;
    @FXML private Button btnGoHome;
    @FXML private Label  lbError;

    private final Map<String, BooleanProperty> progLangState = new HashMap<>();
    private final Map<String, BooleanProperty> dbState = new HashMap<>();

    @FXML
    private void initialize() {
        cbAcademicStatus.setItems(FXCollections.observableArrayList(
                "Freshman", "Sophomore", "Junior", "Senior", "Graduate"
        ));

        ObservableList<String> plItems = FXCollections.observableArrayList("Python","C++","Java","Javascript","Sql");
        ObservableList<String> dbItems = FXCollections.observableArrayList("MySQL","MongoDB","Sql Server","Oracle");
        lvProgLangs.setItems(plItems);
        lvDatabases.setItems(dbItems);
        lvProgLangs.setCellFactory(checkCellFactory(progLangState));
        lvDatabases.setCellFactory(checkCellFactory(dbState));

        cbRole.setItems(FXCollections.observableArrayList(
                "Data Scientist","Data Analyst","Ai Engineer","Software Engineer","Cybersecurity Analyst","Backend Developer"));
        cbRole.setEditable(true);

        tgJob.selectedToggleProperty().addListener((obs,o,n)->{
            boolean employed = (n == rbEmployed);
            taJobDetails.setDisable(!employed);
            if (!employed) taJobDetails.clear();
        });
        rbUnemployed.setSelected(true);

        taNewComment.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode()== KeyCode.ENTER) onAddComment(null);
        });

        cbWhitelist.selectedProperty().addListener((o,ov,nv)->{ if(nv) cbBlacklist.setSelected(false);});
        cbBlacklist.selectedProperty().addListener((o,ov,nv)->{ if(nv) cbWhitelist.setSelected(false);});
    }

    private Callback<ListView<String>, ListCell<String>> checkCellFactory(Map<String, BooleanProperty> stateMap) {
        return CheckBoxListCell.forListView(item -> stateMap.computeIfAbsent(item, k -> new SimpleBooleanProperty(false)));
    }

    private String toTitleCaseEachWord(String s) {
        if (s == null) return "";
        String t = s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+"," ");
        if (t.isEmpty()) return "";
        StringBuilder b = new StringBuilder();
        for (String p : t.split(" ")) {
            if (p.isEmpty()) continue;
            b.append(Character.toUpperCase(p.charAt(0)));
            if (p.length()>1) b.append(p.substring(1));
            b.append(" ");
        }
        return b.toString().trim();
    }

    private boolean containsIgnoreCase(ObservableList<String> items, String value) {
        for (String s : items) if (s.equalsIgnoreCase(value)) return true;
        return false;
    }

    @FXML private void onAddLang(ActionEvent e){
        String norm = toTitleCaseEachWord(tfNewLang.getText());
        if (norm.isEmpty()) return;
        if (!containsIgnoreCase(lvProgLangs.getItems(), norm)) lvProgLangs.getItems().add(norm);
        progLangState.computeIfAbsent(norm,k->new SimpleBooleanProperty(true)).set(true);
        lvProgLangs.refresh(); tfNewLang.clear();
    }

    @FXML private void onAddDb(ActionEvent e){
        String norm = toTitleCaseEachWord(tfNewDb.getText());
        if (norm.isEmpty()) return;
        if (!containsIgnoreCase(lvDatabases.getItems(), norm)) lvDatabases.getItems().add(norm);
        dbState.computeIfAbsent(norm,k->new SimpleBooleanProperty(true)).set(true);
        lvDatabases.refresh(); tfNewDb.clear();
    }

    @FXML private void onAddRole(ActionEvent e){
        String norm = toTitleCaseEachWord(tfNewRole.getText());
        if (norm.isEmpty()) return;
        if (!containsIgnoreCase(cbRole.getItems(), norm)) cbRole.getItems().add(norm);
        cbRole.setValue(norm);
        tfNewRole.clear();
    }

    @FXML private void onUpdatePhoto(ActionEvent e){
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Photo");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files","*.png","*.jpg","*.jpeg","*.gif","*.bmp"));
        File f = fc.showOpenDialog(btnUpdatePhoto.getScene().getWindow());
        if (f != null) {
            Image img = new Image(f.toURI().toString(),140,140,true,true);
            ivPhoto.setImage(img);
        }
    }

    @FXML private void onAddComment(ActionEvent e){
        String text = taNewComment.getText()==null? "": taNewComment.getText().trim();
        if (text.isEmpty()) return;
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        lvComments.getItems().add(ts + " - " + text);
        taNewComment.clear();
    }

    @FXML private void onClear(ActionEvent e){
        tfFullName.clear();
        cbAcademicStatus.getSelectionModel().clearSelection();
        rbUnemployed.setSelected(true);
        taJobDetails.clear(); taJobDetails.setDisable(true);
        progLangState.values().forEach(p->p.set(false));
        dbState.values().forEach(p->p.set(false));
        cbRole.getSelectionModel().clearSelection(); cbRole.getEditor().clear();
        tfNewRole.clear(); tfNewLang.clear(); tfNewDb.clear();
        taNewComment.clear(); lvComments.getItems().clear();
        cbWhitelist.setSelected(false); cbBlacklist.setSelected(false);
        lbError.setText(""); ivPhoto.setImage(null);
    }

    @FXML private void onSave(ActionEvent e){
        lbError.setText("");
        String name = tfFullName.getText()==null? "": tfFullName.getText().trim();
        if (name.isEmpty()) { fail("Full Name is required."); return; }
        if (cbAcademicStatus.getValue()==null){ fail("Academic Status is required."); return; }
        boolean employed = rbEmployed.isSelected();
        if (!employed && !rbUnemployed.isSelected()){ fail("Current Job Status is required."); return; }
        if (employed){
            String jd = taJobDetails.getText()==null? "": taJobDetails.getText().trim();
            if (jd.isEmpty()){ fail("Job Details are required for employed students."); return; }
        }
        List<String> chosenPL = checkedItems(progLangState);
        List<String> chosenDB = checkedItems(dbState);
        if (chosenPL.isEmpty()){ fail("Select at least one Programming Language."); return; }
        if (chosenDB.isEmpty()){ fail("Select at least one Database."); return; }

        String role = cbRole.getValue();
        if (role==null || role.trim().isEmpty())
            role = cbRole.getEditor().getText()==null? "": cbRole.getEditor().getText().trim();
        role = toTitleCaseEachWord(role);
        if (role.isEmpty()){ fail("Preferred Role is required."); return; }
        cbRole.setValue(role);

        if (cbWhitelist.isSelected() && cbBlacklist.isSelected()){
            fail("Whitelist and Blacklist cannot both be selected."); return;
        }
        if (existsStudentByFullName(name)){
            fail("Duplicate entry for trimmed full name."); return;
        }

        saveProfile(name, cbAcademicStatus.getValue(), employed,
                taJobDetails.getText(), chosenPL, chosenDB, role,
                new ArrayList<>(lvComments.getItems()),
                cbWhitelist.isSelected(), cbBlacklist.isSelected());

        new Alert(Alert.AlertType.INFORMATION, "Saved.").showAndWait();
    }

    @FXML private void onGoBackHome(ActionEvent e) {
        try {
            HomePageController.goHomeFrom(btnGoHome);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unable to load HomePage.").showAndWait();
        }
    }

    private List<String> checkedItems(Map<String, BooleanProperty> state) {
        List<String> r = new ArrayList<>();
        for (Map.Entry<String, BooleanProperty> e : state.entrySet()) {
            if (e.getValue().get()) r.add(e.getKey());
        }
        return r;
    }

    private void fail(String msg){ lbError.setText(msg); }

    @SuppressWarnings("unchecked")
    private boolean existsStudentByFullName(String trimmedFullName){
        try{
            Class<?> daoClass = Class.forName("cs151.application.studentprofile.StudentDAO");
            Object r = daoClass.getMethod("existsByFullName", String.class).invoke(null, trimmedFullName);
            if (r instanceof Boolean) return (Boolean) r;
        }catch(Exception ignored){}
        return false;
    }

    @SuppressWarnings("unchecked")
    private void saveProfile(String name, String status, boolean employed,
                             String jobDetails, List<String> langs, List<String> dbs,
                             String role, List<String> comments,
                             boolean whitelist, boolean blacklist){
        try{
            Class<?> daoClass = Class.forName("cs151.application.studentprofile.StudentDAO");
            daoClass.getMethod("saveFromUI",
                    String.class, String.class, boolean.class, String.class,
                    List.class, List.class, String.class, List.class,
                    boolean.class, boolean.class
            ).invoke(null,
                    name, status, employed, jobDetails,
                    langs, dbs, role, comments,
                    whitelist, blacklist
            );
        }catch(Exception ignored){}
    }
}


