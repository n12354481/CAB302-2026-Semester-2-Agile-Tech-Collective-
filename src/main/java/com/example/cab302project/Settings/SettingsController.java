package com.example.cab302project.Settings;

import com.example.cab302project.Database.DatabaseSchema;
import com.example.cab302project.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SettingsController {
    private int userId;
    private SettingsModel settings;
    private ISettingsDAO settingsDAO;

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

    @FXML
    private CheckBox communityParticipationCheckBox;
    @FXML
    private CheckBox activityParticipationCheckbox;
    @FXML
    private CheckBox checkinParticipationCheckbox;
    @FXML
    private CheckBox aiCheckbox;
    @FXML
    private CheckBox aiActivityCheckbox;
    @FXML
    private CheckBox aiCheckinCheckbox;

    public SettingsController() {
        settingsDAO = new DatabaseSchema.SettingsDAO();
    }

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

    public void setUserId(int userId) {
        this.userId = userId;
        loadSettings();
    }

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
