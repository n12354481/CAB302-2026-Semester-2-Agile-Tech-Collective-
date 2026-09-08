package com.example.cab302project.Settings;

import com.example.cab302project.SettingsDAO;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class SettingsController {
    private int userId;
    private SettingsModel settings;
    private ISettingsDAO settingsDAO;

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
        settingsDAO = new SettingsDAO();
    }

    public void setUserId(int UserId) {
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
    }

}
