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


    public SettingsController() {

        settingsDAO = new DatabaseSettingsDAO();

        settings = settingsDAO.getSettings(userId);

        if(settings == null) {
            settingsDAO.insertDefaultSettings(userId);
            settings=settingsDAO.getSettings(userId);
        }
    }

    //Navigation methods
    @FXML
    private void onAccountButtonClicked() { show(account); }

    @FXML
    private void onPrivacyButtonClicked() { show(privacy); }

    @FXML
    private void onDataButtonClicked() { show(data); }

    @FXML
    private void onAIButtonClicked() { show(ai); }



    private void show(AnchorPane section)
    {
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

    //UserID
    public void setUserId(int userId) {
        this.userId = userId;

        loadSettings();
        loadUserDetails();
    }

    //Loading settings onto the page.
    private void loadSettings() {
        settings = settingsDAO.getSettings(userId);

        if(settings == null)
        {
            return;
        }

        communityParticipationCheckBox.setSelected(settings.isCommunityParticipation());
        activityParticipationCheckbox.setSelected(settings.isCommunityActivityParticipation());
        checkinParticipationCheckbox.setSelected(settings.isCommunityCheckinParticipation());
        aiCheckbox.setSelected(settings.AIPersonalisationEnabled());
        aiActivityCheckbox.setSelected(settings.AIActivityPersonalisationEnabled());
        aiCheckinCheckbox.setSelected(settings.AICheckinPersonalisationEnabled());
    }

    private void loadUserDetails()
    {

        //Need to update the code once Pahal finishes the UserDAO.

//        UserDAO userDAO = new DatabaseSchema.UserDAO();
//        User user = userDAO.getUserById(userId);
//
//        emailTextField.setText(user.getEmail());
//        usernameTextField.setText(user.getUsername());

//        emailUpdateButton.setText("Update");
//        usernameUpdateButton.setText("Update");
    }

    @FXML
    private void onEmailUpdateClicked() {
        if(!emailTextField.isEditable()) {
            emailTextField.setEditable(true);
            emailTextField.requestFocus();
            emailUpdateButton.setText("Done");
        } else {
            emailTextField.setEditable(false);
            emailUpdateButton.setText("Update");

            //Need to save the new email to the dao using userDAO.updateEmail or something.

        }
    }

    @FXML
    private void onUsernameUpdateClicked() {
        if(!usernameTextField.isEditable()) {
            usernameTextField.setEditable(true);
            usernameTextField.requestFocus();
            usernameUpdateButton.setText("Done");
        } else {
            usernameTextField.setEditable(false);
            usernameUpdateButton.setText("Update");

            //Need to save the new username to the dao using userDAO.updateUsername or something.
        }
    }

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

    @FXML
    private void onConfirmPasswordClicked()
    {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if(currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank())
        {
            showError("Please fill all fields.");
            return;
        }

        if(currentPassword.equals(newPassword))
        {
            showError("Please ensure that your new password is different to your old password.");
            return;
        }

        if(!newPassword.equals(confirmPassword))
        {
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

    private void showError(String message)
    {
        passwordError.setText(message);
        passwordError.setVisible(true);
        passwordError.setManaged(true);
    }


    @FXML
    private void onCancelPasswordClicked()
    {
        passwordOverlay.setVisible(false);
        passwordOverlay.setManaged(false);

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }


    @FXML
    private void onSaveSettings() {
        settings.setCommunityParticipation(communityParticipationCheckBox.isSelected());
        settings.setActivityDataParticipation(activityParticipationCheckbox.isSelected());
        settings.setCheckinDataParticipation(checkinParticipationCheckbox.isSelected());
        settings.setAIPersonalisation(aiCheckbox.isSelected());
        settings.setAIActivityPersonalisation(aiActivityCheckbox.isSelected());
        settings.setAICheckinPersonalisation(aiCheckinCheckbox.isSelected());

        settingsDAO.saveSettings(userId, settings);

        System.out.println("Settings saved for user: " + userId);
    }
}
