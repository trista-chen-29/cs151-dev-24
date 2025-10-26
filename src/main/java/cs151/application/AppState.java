package cs151.application;

import cs151.application.studentprofile.StudentDirectoryService;

public final class AppState {
    private AppState() {}
    public static String directoryQuery = "";
    public static StudentDirectoryService.FilterMode directoryMode =
            StudentDirectoryService.FilterMode.ALL;

    public static void clearDirectorySearch() {
        directoryQuery = "";
        directoryMode = StudentDirectoryService.FilterMode.ALL;
    }
}
