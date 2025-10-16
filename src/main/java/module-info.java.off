module cs151.application {
    requires javafx.controls;
    requires javafx.fxml;
<<<<<<< HEAD
    requires java.sql;
    requires org.xerial.sqlitejdbc; // SQLite driver: ship via Maven
    requires com.dlsc.formsfx; // 3rd-party UI lib

    // FXML controllers:
    opens cs151.application.homepage to javafx.fxml; // HomePageController
    opens cs151.application.programminglanguages to javafx.fxml; // PLController

    // If TableView binds model fields via PropertyValueFactory:
    opens cs151.application.studentprofile to javafx.base;       // for Student getters

    /* ---- Exports (only if other modules compile against these APIs) ---- */
    exports cs151.application;
    exports cs151.application.programminglanguages;
    exports cs151.application.homepage;
    exports cs151.application.studentprofile;
}
=======
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires com.dlsc.formsfx;

    // FXML controllers
    opens cs151.application.homepage to javafx.fxml;

    // one 'opens' per package (no duplicates)
    opens cs151.application.programminglanguages to javafx.fxml, javafx.base;
    opens cs151.application.studentprofile       to javafx.fxml, javafx.base;

    // models in root package for TableView reflection
    opens cs151.application to javafx.base;

    // exports
    exports cs151.application;
    exports cs151.application.homepage;
    exports cs151.application.programminglanguages;
    exports cs151.application.studentprofile;
}
>>>>>>> f03f6f4 (Initial commit: JavaFX project with ignore rules)
