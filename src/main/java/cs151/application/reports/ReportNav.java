package cs151.application.reports;

import cs151.application.AppState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ReportNav {
    static void openStudentDetail(Node from, long id, String name) {
        try{
            //TODO: Change resource to implemented report-student-detail.fxml
            FXMLLoader fx = new FXMLLoader(ReportNav.class.getResource("/cs151/application/view-student-profile.fxml"));
            Parent root = fx.load();
            from.getScene().setRoot(root);

        }catch(Exception ex){ex.printStackTrace();}
    }

  public static void openCommentViewer(Node From, String date, String commentText) {
        try {
           Parent currentNode = From.getScene().getRoot();
           AppState.pushPageHistory(currentNode);

            FXMLLoader fx = new FXMLLoader(ReportNav.class.getResource("/cs151/application/report-comment.fxml"));
            Parent root = fx.load();
            ReportCommentController ctrl = fx.getController(); // ReportCommentController
            ctrl.load(commentText,date);
            From.getScene().setRoot(root);

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void backToReports(Node from) {
        try {
            FXMLLoader fx = new FXMLLoader(ReportNav.class.getResource("/cs151/application/reports.fxml"));
            Parent root = fx.load();
            from.getScene().setRoot(root);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

   public static void backToStudent(Node From) {
       try {
           Parent root = AppState.popPageHistory();
           From.getScene().setRoot(root);
       } catch (Exception ex) { ex.printStackTrace(); }
    }
}
