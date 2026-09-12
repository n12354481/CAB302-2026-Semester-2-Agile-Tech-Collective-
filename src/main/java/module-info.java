module com.example.cab302project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.compiler;

    opens com.example.cab302project to javafx.fxml;
    opens com.example.cab302project.Dashboard to javafx.fxml;
    opens com.example.cab302project.Settings to javafx.fxml;
    opens com.example.cab302project.Authentication to javafx.fxml;
    opens com.example.cab302project.Database to javafx.fxml;
    opens com.example.cab302project.MoodForm to javafx.fxml;

    exports com.example.cab302project;
    exports com.example.cab302project.Settings;
    exports com.example.cab302project.Authentication;
    exports com.example.cab302project.Database;
    exports com.example.cab302project.Dashboard;
}