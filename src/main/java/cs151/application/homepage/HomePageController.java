package cs151.application.homepage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class HomePageController {

    private static final String THEME_CSS = "/cs151/application/theme.css";

    @FXML
    private void onGoToLanguages(ActionEvent event) {
        String[] candidates = new String[] {
                "/cs151/application/define-pl.fxml",
                "/cs151/application/programminglanguages/define-pl.fxml"
        };
        switchSceneKeepingStyles(event, candidates, "Cannot open Programming Languages");
    }

    @FXML
    private void onGoToStudentProfile(ActionEvent event) {
        String[] candidates = new String[] {
                "/cs151/application/studentprofile/studentprofile.fxml",
                "/cs151/application/studentprofile.fxml"
        };
        switchSceneKeepingStyles(event, candidates, "Cannot open Student Profile");
    }

    @FXML
    private void onSearch(ActionEvent event) {
        // TODO implement search later
    }

    @FXML
    private void onApplyFilters(ActionEvent event) {
        // TODO implement filters later
    }

    public static void goHomeFrom(Node source) {
        setRootKeepingStyles("/cs151/application/homepage.fxml", source);
    }

    public static void goHomeFrom(Button source) {
        goHomeFrom((Node) source);
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

    private void switchSceneKeepingStyles(ActionEvent event, String[] resourceCandidates, String errorHeader) {
        Exception lastError = null;
        for (String res : resourceCandidates) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource(res));
                Scene scene = ((Node) event.getSource()).getScene();
                scene.setRoot(root);

                boolean hasTheme = scene.getStylesheets().stream().anyMatch(s -> s.endsWith("theme.css"));
                if (!hasTheme) {
                    var url = getClass().getResource(THEME_CSS);
                    if (url != null) scene.getStylesheets().add(url.toExternalForm());
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
