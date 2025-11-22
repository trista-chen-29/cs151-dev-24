package cs151.application.reports;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ReportCommentController {
    public TextArea taBody;
    public TextField tfCreated;

    @FXML
    public void onBackToProfile() {
        ReportNav.backToStudent(taBody.getScene().getRoot());
    }

   public void load(String comment, String date){
        taBody.setText(comment);
        tfCreated.setText(date);
   }
}
