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
    //Do that for the remaining 5 checkboxes.


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

        //...


    }

    @FXML
    private void onSaveSettings() {
        settings.setCommunityParticipation(communityParticipationCheckBox.isSelected());

        //...

        settingsDAO.saveSettings(userId, settings);
    }

}
