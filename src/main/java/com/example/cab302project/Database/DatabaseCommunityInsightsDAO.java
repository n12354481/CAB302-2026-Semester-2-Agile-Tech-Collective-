package com.example.cab302project.Database;

import com.example.cab302project.Dashboard.CommunityInsights.ICommunityInsightsDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class aims to create the DAO for the community insights data.
 */
public class DatabaseCommunityInsightsDAO implements ICommunityInsightsDAO {
    private Connection connection;

    /**
     * Constructs the database connection.
     */
    public DatabaseCommunityInsightsDAO() {
        connection = DatabaseConnection.getInstance();
    }


    /**
     * Method which aims to fetch the total number of participating users.
     * @return: Returns the total number of participating users.
     */
    @Override
    public int getParticipatingUserCount() {
        String query = """
                SELECT COUNT(*) FROM users u JOIN settings s ON u.userID = s.user_id
                WHERE s.community_participation = 1
                """;

        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Method which aims to fetch the average sleep for participating users.
     * @return: Returns the average sleep for participating users.
     */
    @Override
    public double getAverageSleep() {
        String query = """
                SELECT AVG(c.sleep) FROM checkin c
                JOIN settings s ON c.userID = s.user_id
                WHERE s.community_participation = 1
                AND s.checkin_data_participation = 1              
                AND c.sleep IS NOT NULL
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Method which aims to fetch the average study stress for participating users.
     * @return: Returns the average study stress for participating users.
     */
    @Override
    public double getAverageStudyStress() {
        String query = """
                SELECT AVG(c.study_stress) FROM checkin c
                JOIN settings s ON c.userID = s.user_id
                WHERE s.community_participation = 1
                AND s.checkin_data_participation = 1                
                AND c.study_stress IS NOT NULL
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Method which aims to fetch the average water for participating users.
     * @return: Returns the average water for participating users.
     */
    @Override
    public double getAverageWater() {
        String query = """
                SELECT AVG(c.water) FROM checkin c
                JOIN settings s ON c.userID = s.user_id
                WHERE s.community_participation = 1
                AND s.checkin_data_participation = 1    
                AND c.water IS NOT NULL
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Method which aims to fetch the average emotion for participating users.
     * @return: Returns the average emotion for participating users.
     */
    @Override
    public double getAverageEmotion() {
        String query = """
                SELECT AVG(c.emotion_today) FROM checkin c
                JOIN settings s ON c.userID = s.user_id
                WHERE s.community_participation = 1
                AND s.checkin_data_participation = 1  
                AND c.emotion_today IS NOT NULL
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Method which aims to fetch the total activity minutes for participating users.
     * @return: Returns the total activity minutes for participating users.
     */
    @Override
    public int getTotalActivityMinutes() {
        String query = """
                SELECT COALESCE(SUM(al.minutes), 0) FROM activity_log al
                JOIN settings s ON al.userID = s.user_id
                WHERE s.community_participation = 1
                AND s.activity_data_participation = 1
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Method which aims to fetch the most popular activity category for participating users.
     * @return: Returns the most popular activity category for participating users.
     */
    @Override
    public String getMostPopularActivityCategory() {
        String query = """
                SELECT a.category, COUNT(*) AS category_count
                FROM activity_log al
                JOIN activity a ON al.activityID = a.activityID
                JOIN settings s ON al.userID = s.user_id
                WHERE s.community_participation = 1
                AND s.activity_data_participation = 1
                GROUP BY a.category
                ORDER BY category_count DESC 
                LIMIT 1
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString("category");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No data";
    }

    /**
     * Method which aims to fetch the most popular checkin mood for participating users.
     * @return: Returns the most popular checkin mood for participating users.
     */
    @Override
    public String getMostPopularMood() {
        String query = """
                SELECT m.mood_name, COUNT(*) AS mood_count
                FROM checkin_mood cm
                JOIN mood m ON cm.moodID = m.moodID
                JOIN checkin c ON cm.checkinID = c.checkinID
                JOIN settings s ON c.userID = s.user_id
                WHERE s.community_participation = 1
                AND s.checkin_data_participation = 1
                GROUP BY m.mood_name
                ORDER BY mood_count DESC 
                LIMIT 1
                """;
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getString("mood_name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No data";
    }
}
