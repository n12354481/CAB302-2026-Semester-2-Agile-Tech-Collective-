package com.example.cab302project.Database;

import com.example.cab302project.Settings.ISettingsDAO;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSettingsDAO implements ISettingsDAO {
    private Connection connection;
    private int userId;

    public DatabaseSettingsDAO(int userId)
    {
        this.userId = userId;
        connection = DatabaseConnection.getInstance();
        createTable();

    }

    private void createTable()
    {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS settings ("
                    + "user_id INTEGER PRIMARY KEY, "
                    + "community_participation INTEGER, "
                    + "activity_data_participation INTEGER, "
                    + "checkin_data_participation INTEGER, "
                    + "ai_personalisation INTEGER, "
                    + "ai_activity_personalisation INTEGER, "
                    + "ai_checkin_personalisation INTEGER, "
                    + ")";
        } catch (Exception e)
        {

        }
    }

    @Override
    public boolean contributeToCommunityStatistics() {

        return false;
    }

    @Override
    public boolean contributeOnlyActivity() {
        return false;
    }

    @Override
    public boolean contributeOnlyCheckIn() {
        return false;
    }

    @Override
    public boolean AIPersonalisationEnabled() {
        return false;
    }

    @Override
    public boolean AIActivityPersonalisationEnabled() {
        return false;
    }

    @Override
    public boolean AICheckinPersonalisationEnabled() {
        return false;
    }
}
