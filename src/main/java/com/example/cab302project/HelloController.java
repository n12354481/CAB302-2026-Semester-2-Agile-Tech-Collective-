package com.example.cab302project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class HelloController {
    @FXML
    private StackPane mainContent;
    @FXML
    private Button homeButton;
    @FXML
    private Button checkInButton;
    @FXML
    private Button socialButton;
    @FXML
    private Button activitiesButton;
    @FXML
    private Button rewardsButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;

    @FXML
    private void onHomeButtonClicked() {
        loadPage("Dashboard.fxml");
    }

    @FXML
    private void onCheckInButtonClicked() {
        loadPage("CheckIn.fxml");
    }

    @FXML
    private void onActivitiesButtonClicked() {
        loadPage("ActivityMain.fxml");
    }


    @FXML
    private void onRewardsButtonClicked() {
        loadPage("Rewards.fxml");
    }

    @FXML
    private void onSocialButtonClicked() {
        loadPage("Socials.fxml");
    }

    @FXML
    private void onUserProfileButtonClicked() {

        boolean showing = settingsButton.isVisible();
        settingsButton.setVisible(!showing);
        settingsButton.setManaged(!showing);

        logoutButton.setVisible(!showing);
        logoutButton.setManaged(!showing);
    }

    @FXML
    private void onSettingsButtonClicked() {
        loadPage("Settings.fxml");
    }

    @FXML
    private void onLogoutButtonClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("Login.fxml")
        );

        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) logoutButton.getScene().getWindow();

    }


    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource(page)
            );

            Node content = loader.load();

            mainContent.getChildren().setAll(content);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
