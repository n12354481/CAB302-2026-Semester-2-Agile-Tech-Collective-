package com.example.cab302project;

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
            DatabaseSchema.createAll(connection);
        } catch (SQLException sqlEx) {
            throw new RuntimeException("Could not create the database tables", sqlEx);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("settings.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Nope!");
        stage.setScene(scene);
        stage.show();
    }
}

// hello world, test push
