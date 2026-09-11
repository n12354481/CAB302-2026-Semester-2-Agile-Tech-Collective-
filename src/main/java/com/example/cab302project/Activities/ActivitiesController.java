package com.example.cab302project.Activities;

import com.example.cab302project.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ActivitiesController implements Initializable{

    @FXML
    private StackPane activityContent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void onAllButtonClicked() {
        loadPage("ActivityList.fxml");
    }
    @FXML
    public void onActivitiesButtonClicked() {
        loadPage("ActivitiesList.fxml");
    }

    @FXML
    public void onHobbiesButtonClicked() {
        loadPage("ActivityHobbiesList.fxml");
    }

    private void loadPage(String page) {
        try{
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(page)
            );

            Node content = loader.load();

            activityContent.getChildren().clear();
            activityContent.getChildren().setAll(content);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

