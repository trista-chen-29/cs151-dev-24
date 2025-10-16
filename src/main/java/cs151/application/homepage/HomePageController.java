package cs151.application.homepage;

import cs151.application.programminglanguages.Language;
<<<<<<< HEAD

=======
>>>>>>> f03f6f4 (Initial commit: JavaFX project with ignore rules)
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
<<<<<<< HEAD
=======
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
>>>>>>> f03f6f4 (Initial commit: JavaFX project with ignore rules)
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

<<<<<<< HEAD
=======
import java.net.URL;
>>>>>>> f03f6f4 (Initial commit: JavaFX project with ignore rules)
import java.util.ArrayList;
import java.util.List;

public class HomePageController {
<<<<<<< HEAD
    // FXML fields (must match ids in homepage.fxml)
    @FXML private TextField searchField;

    // Languages
    @FXML private CheckBox plCpp, plJava, plPython, plJs;
    // Databases
    @FXML private CheckBox dbMysql, dbPostgreSQL, dbMongo, dbSqlite;
    // Interests
    @FXML private CheckBox intFullstack, intSWE, intAIML, intDS;
    // “Avoid Blacklists” checkbox
    @FXML private CheckBox avoidBlacklist;

    // Teammate's service (DB/search)
    private final HomePageService svc = new HomePageService();

    /* ====== Handlers called from FXML ====== */
    @FXML private void onGoToLanguages(ActionEvent e) throws Exception {
        switchTo(e, "/cs151/application/define-pl.fxml");
    }
    @FXML private void onAddStudent() {
        // TODO: open your Add Student screen/dialog
        System.out.println("Add Student");
    }
    @FXML private void onSearch()       { runSearch(); }
    @FXML private void onApplyFilters() { runSearch(); }

    /* ====== Delegate search to the service ====== */
=======

    // ====== FXML elements ======
    @FXML private TextField searchField;

    @FXML private CheckBox plCpp, plJava, plPython, plJs;
    @FXML private CheckBox dbMysql, dbPostgreSQL, dbMongo, dbSqlite;
    @FXML private CheckBox intFullstack, intSWE, intAIML, intDS;
    @FXML private CheckBox avoidBlacklist;

    private final HomePageService svc = new HomePageService();

    // ====== Navigation from Home ======
    @FXML
    private void onGoToLanguages(ActionEvent e) throws Exception {
        switchTo(e, "/cs151/application/define-pl.fxml");
    }

    @FXML
    private void onAddStudent(ActionEvent e) {
        onOpenStudentProfile(e);
    }

    // ====== Search and Filters ======
    @FXML private void onSearch()       { runSearch(); }
    @FXML private void onApplyFilters() { runSearch(); }

>>>>>>> f03f6f4 (Initial commit: JavaFX project with ignore rules)
    private void runSearch() {
        String q = searchField != null ? searchField.getText().trim() : "";

        List<Language> langs = new ArrayList<>();
        if (is(plCpp))    langs.add(new Language("C++"));
        if (is(plJava))   langs.add(new Language("Java"));
        if (is(plPython)) langs.add(new Language("Python"));
        if (is(plJs))     langs.add(new Language("Javascript"));

        List<String> dbs = new ArrayList<>();
        if (is(dbMysql))       dbs.add("MySQL");
        if (is(dbPostgreSQL))  dbs.add("PostgreSQL");
        if (is(dbMongo))       dbs.add("MongoDB");
        if (is(dbSqlite))      dbs.add("SQLite");

        List<String> interests = new ArrayList<>();
        if (is(intFullstack)) interests.add("Fullstack Engineer");
        if (is(intSWE))       interests.add("Software Engineer");
        if (is(intAIML))      interests.add("AI/ML Engineer");
        if (is(intDS))        interests.add("Data Scientist");

<<<<<<< HEAD
        boolean blacklistedAllowed = !is(avoidBlacklist); // UI says "Avoid", service expects "allowed"
=======
        boolean blacklistedAllowed = !is(avoidBlacklist);
>>>>>>> f03f6f4 (Initial commit: JavaFX project with ignore rules)

        var results = svc.search(q, langs, dbs, interests, blacklistedAllowed);
        System.out.println("Found " + results.size() + " students");
        // TODO: bind results to a TableView/ListView
    }

    private boolean is(CheckBox cb) { return cb != null && cb.isSelected(); }

    private void switchTo(ActionEvent e, String fxmlPath) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 980, 620);
<<<<<<< HEAD
        scene.getStylesheets().add(
                getClass().getResource("/cs151/application/theme.css").toExternalForm()
        );
=======
        URL css = getClass().getResource("/cs151/application/theme.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    // ====== Open Student Profile ======
    @FXML
    private void onOpenStudentProfile(ActionEvent event) {
        try {
            URL found = findStudentProfileFxml();
            if (found == null) {
                new Alert(Alert.AlertType.ERROR,
                        "Student Profile FXML not found.\nTried:\n" +
                                "/cs151/application/studentprofile.fxml\n" +
                                "/cs151/application/studentprofile/student-profile.fxml\n" +
                                "/cs151/application/studentprofile/StudentProfile.fxml\n" +
                                "/cs151/application/studentprofile/studentProfile.fxml\n" +
                                "/cs151/application/studentprofile/Student-Profile.fxml"
                ).showAndWait();
                return;
            }
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(found);
            Scene scene = new Scene(loader.load(), 980, 620);
            URL css = getClass().getResource("/cs151/application/theme.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Student Profile page.").showAndWait();
        }
    }

    private URL findStudentProfileFxml() {
        // Try several possible file paths / names
        String[] candidates = new String[] {
                "/cs151/application/studentprofile.fxml",
                "/cs151/application/studentprofile/student-profile.fxml",
                "/cs151/application/studentprofile/StudentProfile.fxml",
                "/cs151/application/studentprofile/studentProfile.fxml",
                "/cs151/application/studentprofile/Student-Profile.fxml"
        };
        for (String p : candidates) {
            URL u = getClass().getResource(p);
            if (u != null) return u;
        }
        return null;
    }

    // ====== Global navigation back to Home ======
    public static void goHomeFrom(Node anyNode) throws Exception {
        Stage stage = (Stage) anyNode.getScene().getWindow();
        String[] candidates = new String[] {
                "/cs151/application/homepage.fxml",
                "/cs151/application/homepage/homepage.fxml"
        };
        URL found = null;
        for (String p : candidates) {
            URL u = HomePageController.class.getResource(p);
            if (u != null) { found = u; break; }
        }
        if (found == null) {
            throw new IllegalStateException(
                    "Home FXML not found. Tried: /cs151/application/homepage.fxml and /cs151/application/homepage/homepage.fxml");
        }
        FXMLLoader loader = new FXMLLoader(found);
        Scene scene = new Scene(loader.load(), 980, 620);
        URL css = HomePageController.class.getResource("/cs151/application/theme.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
>>>>>>> f03f6f4 (Initial commit: JavaFX project with ignore rules)
        stage.setScene(scene);
        stage.show();
    }
}
