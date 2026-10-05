package com.example.cab302project;

import com.example.cab302project.Authentication.IUserDAO;
import com.example.cab302project.Authentication.User;
import com.example.cab302project.Dashboard.DashboardController;
import com.example.cab302project.Database.DatabaseUserDAO;
import com.example.cab302project.Rewards.RewardsController;
import com.example.cab302project.Settings.SettingsController;
import com.example.cab302project.SocialPostings.SocialPostingsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Stack;

/**
 * This class acts as the main controller which loads the app pages with a proper sidebar.
 */
public class HelloController {
    private int userID;
    private User user;
    private IUserDAO userDAO = new DatabaseUserDAO();

    @FXML
    private StackPane mainContent;

    @FXML
    private Label usernameDisplay;

    @FXML
    private Label usernameInitials;

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
        user = userDAO.getUserId(userID);
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

//        boolean showing = settingsButton.isVisible();
//        settingsButton.setVisible(!showing);
//        settingsButton.setManaged(!showing);
//
//        logoutButton.setVisible(!showing);
//        logoutButton.setManaged(!showing);
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
        // Swap the root (not the scene) so the window keeps its size.
        logoutButton.getScene().setRoot(loader.load());
    }


    private void loadPage(String page) {
        try {

            String username = user.getUsername();

            usernameDisplay.setText(username);

            String initials;

            if(username.length() >= 2)
            {
                initials = username.substring(0, 2).toUpperCase();
            } else {
                initials = username.toUpperCase();
            }

            usernameInitials.setText(initials);

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

            if(page.equals("socialpostings.fxml")) {
                SocialPostingsController controller = loader.getController();
                controller.setUserId(userID);
            }

            if(page.equals("Rewards.fxml")) {
                RewardsController controller = loader.getController();
                controller.setUserId(userID);
            }

            mainContent.getChildren().setAll(content);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}