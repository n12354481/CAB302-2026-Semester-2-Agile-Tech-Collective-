module com.example.cab302project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.cab302project to javafx.fxml;
    opens com.example.cab302project.Settings to javafx.fxml;

    exports com.example.cab302project;
    exports com.example.cab302project.Settings;
}