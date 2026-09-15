package Database;

import Database.DatabaseConnection;
import Settings.ISettingsDAO;
import Settings.SettingsModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseSettingsDAO implements ISettingsDAO {
    private Connection connection;
    public DatabaseSettingsDAO()
    {
        connection = DatabaseConnection.getInstance();
    }

    //Need to insert default when user registers.
    @Override
    public void insertDefaultSettings(int userId)
    {
        String query = "INSERT INTO settings (user_id) VALUES (?)";
        try {
            PreparedStatement statement = connection.prepareStatement((query));
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public SettingsModel getSettings(int userId)
    {
        String query = "SELECT " +
                "community_participation," +
                "activity_data_participation," +
                "checkin_data_participation," +
                "ai_personalisation," +
                "ai_activity_personalisation," +
                "ai_checkin_personalisation " +
                "FROM settings " +
                "WHERE user_id = ?";
        try{
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                boolean community = result.getBoolean("community_participation");
                boolean activity = result.getBoolean("activity_data_participation");
                boolean checkin = result.getBoolean("checkin_data_participation");
                boolean ai = result.getBoolean("ai_personalisation");
                boolean ai_activity = result.getBoolean("ai_activity_personalisation");
                boolean ai_checkin = result.getBoolean("ai_checkin_personalisation");

                return new SettingsModel(
                        community,
                        activity,
                        checkin,
                        ai,
                        ai_activity,
                        ai_checkin
                );

            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveSettings(int userId, SettingsModel settings)
    {
        String query = "UPDATE settings SET " +
                "community_participation = ?," +
                "activity_data_participation = ?," +
                "checkin_data_participation = ?," +
                "ai_personalisation = ?," +
                "ai_activity_personalisation = ?," +
                "ai_checkin_personalisation = ? " +
                "WHERE user_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBoolean(1, settings.isCommunityParticipation());
            statement.setBoolean(2, settings.isCommunityActivityParticipation());
            statement.setBoolean(3, settings.isCommunityCheckinParticipation());
            statement.setBoolean(4, settings.AIPersonalisationEnabled());
            statement.setBoolean(5, settings.AIActivityPersonalisationEnabled());
            statement.setBoolean(6, settings.AICheckinPersonalisationEnabled());
            statement.setInt(7, userId);

            statement.executeUpdate();

        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
