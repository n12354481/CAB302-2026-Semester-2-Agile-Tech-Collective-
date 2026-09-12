package com.example.cab302project.Settings;

/**
 * An interface which aims to set methods needed for the Settings Database CRUD methods.
 */
public interface ISettingsDAO {
    SettingsModel getSettings(int userId);
    void saveSettings(int userId, SettingsModel settings);
    public void insertDefaultSettings(int userId);
}
