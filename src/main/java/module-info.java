module cs151.application {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // SQLite driver
    requires org.xerial.sqlitejdbc;
    // 3rd-party UI lib
    requires com.dlsc.formsfx;

    opens cs151.application to javafx.fxml;
    opens cs151.application.ProgrammingLanguages to javafx.fxml;
    opens cs151.application.HomePage to javafx.fxml;

    exports cs151.application;
    exports cs151.application.ProgrammingLanguages;
    exports cs151.application.HomePage;
}
