module cab302project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.compiler;

    // FXML needs every package with a controller opened to it.
    opens App to javafx.fxml;
    opens Activities to javafx.fxml;
    opens Authentication to javafx.fxml;
    opens Dashboard to javafx.fxml;
    opens Database to javafx.fxml;
    opens MoodForm to javafx.fxml;
    opens Rewards to javafx.fxml;
    opens Settings to javafx.fxml;
    opens SocialPostings to javafx.fxml;

    exports App;
    exports Activities;
    exports Authentication;
    exports Dashboard;
    exports Database;
    exports MoodForm;
    exports Rewards;
    exports Settings;
    exports SocialPostings;
}
