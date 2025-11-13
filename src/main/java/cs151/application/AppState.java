package cs151.application;

import cs151.application.studentprofile.ViewStudentProfileService;

public final class AppState {
    private AppState() {}
    public static String directoryQuery = "";
    public static ViewStudentProfileService.FilterMode directoryMode =
            ViewStudentProfileService.FilterMode.ALL;
    public static Long preselectStudentId;

    public static void clearDirectorySearch() {
        directoryQuery = "";
        directoryMode = ViewStudentProfileService.FilterMode.ALL;
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
