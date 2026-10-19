
package com.example.cab302project;

import com.example.cab302project.Database.DatabaseConnection;
import com.example.cab302project.Database.DatabaseSchema;
import com.example.cab302project.Rewards.GardenSchema;
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
        try {
            GardenSchema.create(connection);
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Could not create the rewards tables", sqlEx);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Stemwise");
        stage.setScene(scene);
        stage.show();
        stage.setWidth(1000);
        stage.setHeight(1000);
    }
}