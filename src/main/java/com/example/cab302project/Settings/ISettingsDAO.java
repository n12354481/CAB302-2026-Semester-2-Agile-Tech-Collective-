package com.example.cab302project.Settings;

public interface ISettingsDAO {
    SettingsModel getSettings(int userId);
    void saveSettings(int userId, SettingsModel settings);
    public void insertDefaultSettings(int userId);
}
