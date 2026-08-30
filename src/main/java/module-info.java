module com.example.cab302project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.cab302project to javafx.fxml;
    exports com.example.cab302project;
    exports com.example.cab302project.Settings;
}