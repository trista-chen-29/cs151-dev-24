package cs151.application;

import cs151.application.studentprofile.ViewStudentProfileService;

public final class AppState {
    private AppState() {}
    public static String directoryQuery = "";
    public static ViewStudentProfileService.FilterMode directoryMode =
            ViewStudentProfileService.FilterMode.ALL;

    public static void clearDirectorySearch() {
        directoryQuery = "";
        directoryMode = ViewStudentProfileService.FilterMode.ALL;
    }
}
