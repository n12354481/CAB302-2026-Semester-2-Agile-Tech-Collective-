package com.example.cab302project.Settings;

import java.util.ArrayList;

public class MockSettingsDAO implements ISettingsDAO {
    //public final ArrayList<Users> users = new ArrayList<>();
    private int autoIncrementId = 0;

    @Override
    public SettingsModel getSettings(int userId) {

        return null;
    }

    @Override
    public void saveSettings(int userId, SettingsModel settings) {

    }

    @Override
    public void insertDefaultSettings(int userId) {

    }
}
