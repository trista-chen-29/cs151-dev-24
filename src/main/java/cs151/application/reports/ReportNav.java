package cs151.application.reports;

import cs151.application.AppState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ReportNav {
    static void openStudentDetail(Node from, long id, String name) {
        try{
            // Fixed typo: studnet -> student
            FXMLLoader fx = new FXMLLoader(ReportNav.class.getResource("/cs151/application/student-report.fxml"));
            Parent root = fx.load();

            // Pass the student ID to the controller
            ViewStudentReportController ctrl = fx.getController();
            ctrl.loadStudent(id, name);

            from.getScene().setRoot(root);

        }catch(Exception ex){ex.printStackTrace();}
    }

    public static void openCommentViewer(Node From, String date, String commentText) {
        try {
            Parent currentNode = From.getScene().getRoot();
            AppState.pushPageHistory(currentNode);

            FXMLLoader fx = new FXMLLoader(ReportNav.class.getResource("/cs151/application/report-comment.fxml"));
            Parent root = fx.load();
            ReportCommentController ctrl = fx.getController();
            ctrl.load(commentText, date);
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