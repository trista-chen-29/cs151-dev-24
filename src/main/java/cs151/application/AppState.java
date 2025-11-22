package cs151.application;

import cs151.application.studentprofile.ViewStudentProfileService;
import javafx.scene.Parent;

import java.util.Stack;

public final class AppState {
    private AppState() {}
    public static final Stack<Parent> pageHistory = new Stack<>();
    public static String directoryQuery = "";
    public static ViewStudentProfileService.FilterMode directoryMode =
            ViewStudentProfileService.FilterMode.ALL;
    public static Long preselectStudentId;

    public static void clearDirectorySearch() {
        directoryQuery = "";
        directoryMode = ViewStudentProfileService.FilterMode.ALL;
    }
    public static void pushPageHistory(Parent parent){
        pageHistory.push(parent);}
    public static Parent popPageHistory(){
        return pageHistory.pop();
    }
    public static void preselectStudent(long id) {
        preselectStudentId = id;
    }

    public static Long consumePreselectStudent() {
        Long v = preselectStudentId;
        preselectStudentId = null;
        return v;
    }
}
