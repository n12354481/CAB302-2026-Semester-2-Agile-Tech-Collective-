
package App;

import Database.DatabaseConnection;
import Database.DatabaseSchema;
import Rewards.GardenSchema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Connection connection = DatabaseConnection.getInstance();
//        try {
//            //DatabaseSchema.createAll(connection);
//        //} catch (SQLException sqlEx) {
//            throw new RuntimeException("Could not create the database tables", sqlEx);
//       // }
        try {
            GardenSchema.create(connection);
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Could not create the rewards tables", sqlEx);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.setTitle("STEM App");
        stage.setScene(scene);
        stage.show();
    }
}