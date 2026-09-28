package com.example.cab302project.Database;

import com.example.cab302project.Dashboard.IDashboardDAO;
import com.example.cab302project.Dashboard.Recommendations.IRecommendationsDAO;
import com.example.cab302project.MoodForm.CheckIn;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseRecommendationsDAO implements IRecommendationsDAO {
    private Connection connection;

    public DatabaseRecommendationsDAO()
    {
        connection = DatabaseConnection.getInstance();
    }

    @Override
    public List<Map<String, Object>> getRecentActivityData(int userId)
    {
        List<Map<String, Object>> activityData = new ArrayList<>();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(7);

        String query = """
                SELECT
                    al.logID,
                    al.userID,
                    al.activityID,
                    al.log_date,
                    al.minutes,
                    a.activity_name,
                    a.category,
                    a.activity_description,
                    a.goal,
                    a.points
                FROM activity_log al
                JOIN activity a ON al.activityID = a.activityID
                WHERE al.userID = ?
                AND al.log_date BETWEEN ? AND ?
                ORDER BY al.log_date DESC
                """ ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, startDate.toString());
            statement.setString(3, endDate.toString());
            ResultSet result = statement.executeQuery();

            while(result.next())
            {
                Map<String, Object> activity = new HashMap<>();

                activity.put("logID", result.getInt("logID"));
                activity.put("userID", result.getInt("userID"));
                activity.put("activityID", result.getInt("activityID"));
                activity.put("date", result.getInt("log_date"));
                activity.put("minutes", result.getInt("minutes"));
                activity.put("name", result.getInt("activity_name"));
                activity.put("category", result.getInt("category"));
                activity.put("description", result.getInt("description"));
                activity.put("goal", result.getInt("goal"));
                activity.put("points", result.getInt("points"));

                activityData.add(activity);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return activityData;
    }

    @Override
    public List<CheckIn> getRecentCheckinData(int userId)
    {
        List<CheckIn> checkinData = new ArrayList<>();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.minusDays(7);

        String query = "SELECT * FROM checkin WHERE userId = ? AND checkin_date BETWEEN ? AND ? ORDER BY checkin_date DESC;" ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, startDate.toString());
            statement.setString(3, endDate.toString());

            ResultSet result = statement.executeQuery();

            while(result.next())
            {
               int checkinId = result.getInt("checkinID");
               int DBuserId = result.getInt("userID");
               LocalDate checkin_date = result.getDate("checkin_date").toLocalDate();
               int emotion_today = result.getInt("emotion_today");
               int sleep = result.getInt("sleep");
               int water = result.getInt("water");
               int studyStress = result.getInt("study_stress");

               List<String> mood = getCheckinRecentMoods(checkinId);

               CheckIn checkIn = new CheckIn(
                       checkinId,
                       userId,
                       checkin_date,
                       emotion_today,
                       sleep,
                       water,
                       studyStress,
                       mood
               );

                checkinData.add(checkIn);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return checkinData;
    }

    private List<String> getCheckinRecentMoods(int checkinId)
    {
        List<String> mood = new ArrayList<>();

        String query = "SELECT m.mood_name FROM mood m JOIN checkin_mood cm ON m.moodID = cm.moodID WHERE cm.checkinID = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, checkinId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                mood.add(result.getString("mood_name"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return mood;
    }
}
