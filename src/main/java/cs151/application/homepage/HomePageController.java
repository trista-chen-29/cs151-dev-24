package cs151.application.homepage;

import cs151.application.persistence.ProgrammingLanguagesDAO;
import cs151.application.studentprofile.ViewStudentProfileController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class HomePageController {
    private static final String THEME_CSS = "/cs151/application/theme.css";

    // Search
    @FXML private TextField searchField;

    // Show: All / Whitelist / Blacklist (optional segmented control)
    @FXML private ToggleGroup tgShow;
    @FXML private ToggleButton tbAll;
    @FXML private ToggleButton tbWhitelist;
    @FXML private ToggleButton tbBlacklist;

    // Dynamic programming-language filters go here
    @FXML private VBox boxLangFilters;

    // Database filters
    @FXML private CheckBox dbMysql, dbPostgreSQL, dbMongo, dbSqlite;

    // Preferred roles
    @FXML private CheckBox roleFE, roleBE, roleFS, roleData, roleOther;

    // Extra
    @FXML private CheckBox avoidBlacklist;

    private final ProgrammingLanguagesDAO plDao = new ProgrammingLanguagesDAO();

    // ---------- Lifecycle ----------
    @FXML
    private void initialize() {
        refreshLanguageFilters();
        // default to "All" if present
        if (tgShow != null && tbAll != null) tgShow.selectToggle(tbAll);
    }

    private void refreshLanguageFilters() {
        if (boxLangFilters == null) return;
        boxLangFilters.getChildren().clear();
        for (String name : plDao.listAll()) {
            CheckBox cb = new CheckBox(name);
            cb.getStyleClass().add("filter-cb");
            boxLangFilters.getChildren().add(cb);
        }
    }

    // ---------- Top bar ----------
    @FXML
    private void onGoToLanguages(ActionEvent event) {
        String[] candidates = {
                "/cs151/application/define-pl.fxml",
                "/cs151/application/programminglanguages/define-pl.fxml"
        };
        switchScene(event, candidates, "Cannot open Programming Languages");
    }

    // ---------- “Define Student Profile” card ----------
    @FXML
    private void onGoToStudentProfile(ActionEvent event) {
        String[] candidates = { "/cs151/application/define-student-profile.fxml" };
        switchScene(event, candidates, "Cannot open Student Profile");
    }

    // ---- View Student Profiles card ----
    @FXML
    private void onOpenDirectory(ActionEvent event) {
        String[] candidates = { "/cs151/application/view-student-profile.fxml" };
        switchScene(event, candidates, "Cannot open Student Directory");
    }

    // ---------- Search / filters ----------
    @FXML
    private void onSearch(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs151/application/view-student-profile.fxml"));
            Parent root = loader.load(); // <-- after this, FXML fields exist
            ViewStudentProfileController controller = loader.getController();
            String query = searchField.getText().trim(); // your homepage TextField
            controller.updateSearchQuery(query);          // sets it into the profile search bar
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            showError("Cannot open Student Directory", ex);
        }
    }


    @FXML
    private void onRefresh(ActionEvent event) {
        // re-apply filters
        onApplyFilters(event);
    }

    @FXML
    private void onApplyFilters(ActionEvent event) {
        String q = searchField != null && searchField.getText() != null
                ? searchField.getText().trim() : "";

        String mode = (tgShow != null && tgShow.getSelectedToggle() != null)
                ? ((ToggleButton) tgShow.getSelectedToggle()).getText()
                : "All";

        List<String> langs = selectedLanguages();

        boolean wantsMySQL = isSelected(dbMysql);
        boolean wantsPg    = isSelected(dbPostgreSQL);
        boolean wantsMongo = isSelected(dbMongo);
        boolean wantsSqlite= isSelected(dbSqlite);

        boolean fe   = isSelected(roleFE);
        boolean be   = isSelected(roleBE);
        boolean fs   = isSelected(roleFS);
        boolean data = isSelected(roleData);
        boolean other= isSelected(roleOther);

        boolean noBlacklist = isSelected(avoidBlacklist);

        System.out.printf(
                "Apply Filters -> q='%s', show=%s, langs=%s, DBs=[my:%s pg:%s mongo:%s sqlite:%s], roles=[FE:%s BE:%s FS:%s Data:%s Other:%s], avoidBlacklist=%s%n",
                q, mode, langs, wantsMySQL, wantsPg, wantsMongo, wantsSqlite,
                fe, be, fs, data, other, noBlacklist
        );

        // TODO: pass these to your service/DAO to populate the directory table.
    }

    @FXML
    private void onClearFilters(ActionEvent event) {
        if (searchField != null) searchField.clear();
        clearLanguages();
        clear(dbMysql, dbPostgreSQL, dbMongo, dbSqlite,
                roleFE, roleBE, roleFS, roleData, roleOther,
                avoidBlacklist);
        if (tgShow != null && tbAll != null) tgShow.selectToggle(tbAll);
        System.out.println("Filters cleared.");
    }

    // ---------- Public helper to return home ----------
    public static void goHomeFrom(Node source) { setRootKeepingStyles("/cs151/application/homepage.fxml", source); }
    public static void goHomeFrom(Button source) { goHomeFrom((Node) source); }

    // ---------- Internals ----------
    private List<String> selectedLanguages() {
        List<String> out = new ArrayList<>();
        if (boxLangFilters == null) return out;
        for (var n : boxLangFilters.getChildren()) {
            if (n instanceof CheckBox cb && cb.isSelected()) out.add(cb.getText());
        }
        return out;
    }

    private void clearLanguages() {
        if (boxLangFilters == null) return;
        for (var n : boxLangFilters.getChildren()) {
            if (n instanceof CheckBox cb) cb.setSelected(false);
        }
    }

    private static boolean isSelected(CheckBox cb) { return cb != null && cb.isSelected(); }

    private static void clear(CheckBox... boxes) {
        if (boxes == null) return;
        for (CheckBox b : boxes) if (b != null) b.setSelected(false);
    }

    private static void setRootKeepingStyles(String resource, Node source) {
        try {
            Parent root = FXMLLoader.load(HomePageController.class.getResource(resource));
            Scene scene = source.getScene();
            scene.setRoot(root);

            boolean hasTheme = scene.getStylesheets().stream().anyMatch(s -> s.endsWith("theme.css"));
            if (!hasTheme) {
                var url = HomePageController.class.getResource(THEME_CSS);
                if (url != null) scene.getStylesheets().add(url.toExternalForm());
            }
        } catch (Exception ex) {
            showError("Cannot open Home", ex);
        }
    }

    private void switchScene(ActionEvent event, String[] resourceCandidates, String errorHeader) {
        Exception lastError = null;
        for (String res : resourceCandidates) {
            try {
                var url = getClass().getResource(res);
                if (url == null) throw new IllegalStateException("FXML not found on classpath: " + res);
                Parent root = FXMLLoader.load(url);
                Scene scene = ((Node) event.getSource()).getScene();
                scene.setRoot(root);

                boolean hasTheme = scene.getStylesheets().stream().anyMatch(s -> s.endsWith("theme.css"));
                if (!hasTheme) {
                    var themeUrl = getClass().getResource(THEME_CSS);
                    if (themeUrl != null) scene.getStylesheets().add(themeUrl.toExternalForm());
                }
                return;
            } catch (Exception ex) {
                lastError = ex;
            }
        }
        showError(errorHeader, lastError);
    }

    private static void showError(String header, Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.setContentText(ex != null ? ex.getMessage() : "Unknown error");
        a.showAndWait();
        if (ex != null) ex.printStackTrace();
    }
}
