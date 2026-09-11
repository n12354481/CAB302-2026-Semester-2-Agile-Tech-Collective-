package com.example.cab302project.Settings;

import com.example.cab302project.Database.DatabaseSettingsDAO;

import com.example.cab302project.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * The controller class of Settings which handles the integration of the UI and performs appropriate methods based on user's UI interaction.
 */
public class SettingsController {
    private int userId = 1;
    private SettingsModel settings;
    private ISettingsDAO settingsDAO;


    //Account Section fields
    @FXML
    private TextField emailTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Button emailUpdateButton;

    @FXML
    private AnchorPane passwordOverlay;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button usernameUpdateButton;

    @FXML
    private Label passwordError;

    //Sidebar navigation fields
    @FXML
    private Button accountButton;

    @FXML
    private Button privacyButton;

    @FXML
    private Button dataButton;

    @FXML
    private Button aiButton;


    @FXML
    private AnchorPane account;

    @FXML
    private AnchorPane data;

    @FXML
    private AnchorPane privacy;

    @FXML
    private AnchorPane ai;


    //Privacy section fields
    @FXML
    private CheckBox communityParticipationCheckBox;
    @FXML
    private CheckBox activityParticipationCheckbox;
    @FXML
    private CheckBox checkinParticipationCheckbox;

    //AI section fields
    @FXML
    private CheckBox aiCheckbox;
    @FXML
    private CheckBox aiActivityCheckbox;
    @FXML
    private CheckBox aiCheckinCheckbox;


    /**
     * Constructs the DAO for Settings, retrieving the user's settings and initialises the DAO if null.
     */
    public SettingsController() {

        settingsDAO = new DatabaseSettingsDAO();

        settings = settingsDAO.getSettings(userId);

        if (settings == null) {
            settingsDAO.insertDefaultSettings(userId);
            settings = settingsDAO.getSettings(userId);
        }
    }

    /**
     * Initialising the controller in FXML through appropriate conditions.
     */
    @FXML
    private void initialize()
    {
        //Disabling the checkbox for sub-settings topics in differnet pages if the parent checkbox is not checked.

        communityParticipationCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue)
            {
                activityParticipationCheckbox.setDisable(true);
                checkinParticipationCheckbox.setDisable(true);
            } else{
                activityParticipationCheckbox.setDisable(false);
                checkinParticipationCheckbox.setDisable(false);
            }
        });

        aiCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue)
            {
                aiActivityCheckbox.setDisable(true);
                aiCheckinCheckbox.setDisable(true);
            } else{
                aiActivityCheckbox.setDisable(false);
                aiCheckinCheckbox.setDisable(false);
            }
        });
    }

    //Navigation methods for all settings pages which swap the inner content of the settings page through StackPane.
    @FXML
    private void onAccountButtonClicked() {
        show(account);
    }

    @FXML
    private void onPrivacyButtonClicked() {
        show(privacy);
    }

    @FXML
    private void onDataButtonClicked() {
        show(data);
    }

    @FXML
    private void onAIButtonClicked() {
        show(ai);
    }

    /**
     * This method aims to hide different sections in Settings if their corresponding section isn't clicked to view.
     * @param section: The sub-settings content that needs to be overlapped.
     */
    private void show(AnchorPane section) {
        account.setVisible(false);
        account.setManaged(false);

        data.setVisible(false);
        data.setManaged(false);

        ai.setVisible(false);
        ai.setManaged(false);

        privacy.setVisible(false);
        privacy.setManaged(false);

        section.setVisible(true);
        section.setManaged(true);
    }

    /**
     * This method aims to set the userID to the logged-in user.
     * @param userId
     */
    public void setUserId(int userId) {
        this.userId = userId;

        loadSettings();
        loadUserDetails();
    }

    /**
     * This method aims to load the appropriate settings content onto the GUI when initialised.
     */
    private void loadSettings() {

        settings = settingsDAO.getSettings(userId);

        if (settings == null) {
            return;
        }

        //Get appropriate checkboxes GUI according to the latest uploaded Settings.
        communityParticipationCheckBox.setSelected(settings.isCommunityParticipation());
        activityParticipationCheckbox.setSelected(settings.isCommunityActivityParticipation());
        checkinParticipationCheckbox.setSelected(settings.isCommunityCheckinParticipation());
        aiCheckbox.setSelected(settings.AIPersonalisationEnabled());
        aiActivityCheckbox.setSelected(settings.AIActivityPersonalisationEnabled());
        aiCheckinCheckbox.setSelected(settings.AICheckinPersonalisationEnabled());
    }


    private void loadUserDetails() {

        //Need to update the code once Pahal finishes the UserDAO.

//        UserDAO userDAO = new DatabaseSchema.UserDAO();
//        User user = userDAO.getUserById(userId);
//
//        emailTextField.setText(user.getEmail());
//        usernameTextField.setText(user.getUsername());

//        emailUpdateButton.setText("Update");
//        usernameUpdateButton.setText("Update");
    }

    /**
     * This method aims to allow the user to update the Email field only when the update button is clicked.
     */
    @FXML
    private void onEmailUpdateClicked() {
        if (!emailTextField.isEditable()) {
            emailTextField.setEditable(true);
            emailTextField.requestFocus();
            emailUpdateButton.setText("Done");
        } else {
            emailTextField.setEditable(false);
            emailUpdateButton.setText("Update");

            //Need to save the new email to the dao using userDAO.updateEmail or something.

        }
    }

    /**
     * This method aims to allow the user to update the Username field only when the update button is clicked.
     */
    @FXML
    private void onUsernameUpdateClicked() {
        if (!usernameTextField.isEditable()) {
            usernameTextField.setEditable(true);
            usernameTextField.requestFocus();
            usernameUpdateButton.setText("Done");
        } else {
            usernameTextField.setEditable(false);
            usernameUpdateButton.setText("Update");

            //Need to save the new username to the dao using userDAO.updateUsername or something.
        }
    }


    /**
     * This method refreshes the overlay box's fields when changePassword button is clicked for interactive user GUI.
     */
    @FXML
    private void onChangePasswordClicked() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();

        passwordOverlay.setVisible(true);
        passwordOverlay.setManaged(true);

        passwordError.setText("");
        passwordError.setVisible(false);
        passwordError.setManaged(false);

        currentPasswordField.requestFocus();
    }

    /**
     * This method performs validation checks to ensure that the password's are correct and updates it to the database.
     */
    @FXML
    private void onConfirmPasswordClicked() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            showError("Please fill all fields.");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            showError("Please ensure that your new password is different to your old password.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Password do not match. Please enter the same password for both new and cofirm password fields.");
            return;
        }

        //Need to verify current password
        //Need to hash new password and save it to db using the UserDAO.


        passwordOverlay.setVisible(false);
        passwordOverlay.setManaged(false);

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    /**
     * This method aims to show an error message in the password overlay box when there is a validation error.
     * @param message: The error shown to the user.
     */
    private void showError(String message) {
        passwordError.setText(message);
        passwordError.setVisible(true);
        passwordError.setManaged(true);
    }


    /**
     * This method hides the password overlay box when the user's changes their mind on updating the password.
     */
    @FXML
    private void onCancelPasswordClicked() {
        passwordOverlay.setVisible(false);
        passwordOverlay.setManaged(false);

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }


    /**
     * This method saves the settings performed in the GUI to the database.
     */
    @FXML
    private void onSaveSettings() {
        if(communityParticipationCheckBox.isSelected())
        {
            activityParticipationCheckbox.setDisable(false);
            checkinParticipationCheckbox.setDisable(false);

            settings.setActivityDataParticipation(activityParticipationCheckbox.isSelected());
            settings.setCheckinDataParticipation(checkinParticipationCheckbox.isSelected());
        } else {
            activityParticipationCheckbox.setDisable(true);
            checkinParticipationCheckbox.setDisable(true);

            settings.setActivityDataParticipation(false);
            settings.setCheckinDataParticipation(false);
        }

        if(aiCheckbox.isSelected())
        {
            aiActivityCheckbox.setDisable(false);
            aiCheckinCheckbox.setDisable(false);

            settings.setAICheckinPersonalisation(aiCheckinCheckbox.isSelected());
            settings.setAIActivityPersonalisation(aiActivityCheckbox.isSelected());
        } else {
            aiActivityCheckbox.setDisable(true);
            aiCheckinCheckbox.setDisable(true);

            settings.setAICheckinPersonalisation(false);
            settings.setAIActivityPersonalisation(false);
        }

        settings.setCommunityParticipation(communityParticipationCheckBox.isSelected());
        settings.setAIPersonalisation(aiCheckbox.isSelected());

        settingsDAO.saveSettings(userId, settings);

        System.out.println("Settings saved for user: " + userId);
    }
}
