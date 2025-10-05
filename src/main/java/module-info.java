module cs151.application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // SQLite driver: harmless to keep; optional if run on classpath
    requires org.xerial.sqlitejdbc;
    // 3rd-party UI lib already use
    requires com.dlsc.formsfx;

    /* ---- Open controller packages to FXMLLoader ---- */
    opens cs151.application to javafx.fxml;                   // HomeController
    opens cs151.application.ProgrammingLanguages to javafx.fxml; // PLController
    opens cs151.application.HomePage to javafx.fxml;       // HomePageController, SearchController
    /* ---- If bind POJOs in TableView via reflection (optional) ---- */
    // opens cs151.application.model to javafx.base;

    /* ---- Exports (only if other modules compile against these APIs) ---- */
    exports cs151.application;
    exports cs151.application.ProgrammingLanguages;
    exports cs151.application.HomePage;
}