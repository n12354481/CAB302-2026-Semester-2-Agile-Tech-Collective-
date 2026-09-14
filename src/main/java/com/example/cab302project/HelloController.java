package com.example.cab302project;

import com.example.cab302project.Dashboard.DashboardController;
import com.example.cab302project.Settings.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

/**
 * This class acts as the main controller which loads the app pages with a proper sidebar.
 */
public class HelloController {
    private int userID;
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

    /**
     * This method aims to load the dashboard as the initial page with the logged-in user's ID.
     * @param userID
     */
    public void setUserID(int userID)
    {
        this.userID = userID;
        loadPage("Dashboard.fxml");
    }

    @FXML
    private void onHomeButtonClicked() {
        loadPage("Dashboard.fxml");
    }

    @FXML
    private void onCheckInButtonClicked() {
        loadPage("mood-checkin.fxml");
    }

    @FXML
    private void onActivitiesButtonClicked() {
        loadPage("Activities.fxml");
    }


    @FXML
    private void onRewardsButtonClicked() {
        loadPage("Rewards.fxml");
    }

    @FXML
    private void onSocialButtonClicked() {
        loadPage("socialpostings.fxml");
    }

    public void loadSocialPage() {
        loadPage("socialpostings.fxml");
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
        loadPage("settings.fxml");
    }

    @FXML
    private void onLogoutButtonClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("login.fxml")
        );
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(page)
            );

            Node content = loader.load();

            if(page.equals("Dashboard.fxml")) {
                DashboardController controller = loader.getController();
                controller.setUserId(userID);
            }

            if(page.equals("settings.fxml"))
            {
                SettingsController controller = loader.getController();
                controller.setUserId(userID);
            }

            mainContent.getChildren().setAll(content);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}