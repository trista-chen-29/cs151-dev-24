package cs151.application.homepage;

import cs151.application.programminglanguages.Language;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HomePageController {
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

        boolean blacklistedAllowed = !is(avoidBlacklist); // UI says "Avoid", service expects "allowed"

        var results = svc.search(q, langs, dbs, interests, blacklistedAllowed);
        System.out.println("Found " + results.size() + " students");
        // TODO: bind results to a TableView/ListView
    }

    private boolean is(CheckBox cb) { return cb != null && cb.isSelected(); }

    private void switchTo(ActionEvent e, String fxmlPath) throws Exception {
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 980, 620);
        scene.getStylesheets().add(
                getClass().getResource("/cs151/application/theme.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.show();
    }
}
