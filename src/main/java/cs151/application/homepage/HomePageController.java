package cs151.application.homepage;

import cs151.application.AppState;
import cs151.application.studentprofile.ViewStudentProfileService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class HomePageController {
    private static final String THEME_CSS = "/cs151/application/theme.css";

    // Search input
    @FXML private TextField searchField;

    @FXML
    private void initialize() {}

    // ---------- Top bar ----------
    @FXML
    private void onGoToLanguages(ActionEvent event) {
        switchScene(event, new String[]{
                "/cs151/application/define-programming-language.fxml",
                "/cs151/application/programminglanguages/define-programming-language.fxml"
        }, "Cannot open Programming Languages");
    }

    // ---------- Define Student Profile ----------
    @FXML
    private void onGoToStudentProfile(ActionEvent event) {
        switchScene(event, new String[]{ "/cs151/application/define-student-profile.fxml" },
                "Cannot open Student Profile");
    }

    // ---- View Student Profiles ----
    @FXML
    private void onOpenDirectory(ActionEvent event) {
        switchScene(event, new String[]{ "/cs151/application/view-student-profile.fxml" },
                "Cannot open Student Directory");
    }

    // ---------- Search ----------
    @FXML
    private void onSearch(ActionEvent event) {
        AppState.directoryQuery = (searchField != null && searchField.getText() != null)
                ? searchField.getText().trim() : "";
        AppState.directoryMode = ViewStudentProfileService.FilterMode.ALL; // no toggles on homepage
        switchScene(event, new String[]{ "/cs151/application/view-student-profile.fxml" },
                "Cannot open Student Directory");
    }

    // ---------- Public helper to return home ----------
    public static void goHomeFrom(Node source) { setRootKeepingStyles("/cs151/application/homepage.fxml", source); }
    public static void goHomeFrom(Button source) { goHomeFrom((Node) source); }

    // ---------- Internals ----------
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
