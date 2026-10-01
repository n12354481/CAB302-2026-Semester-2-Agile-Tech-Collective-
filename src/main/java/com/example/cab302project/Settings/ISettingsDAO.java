package com.example.cab302project.Settings;

import java.time.LocalDate;

/**
 * An interface which aims to set methods needed for the Settings Database CRUD methods.
 */
public interface ISettingsDAO {
    SettingsModel getSettings(int userId);
    void saveSettings(int userId, SettingsModel settings);
    public void insertDefaultSettings(int userId);

    void deleteActivities(int userId);
    void deleteCheckin(int userId);
    void deleteAccount(int userId);
}
