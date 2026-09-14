package com.example.cab302project.Database;

import com.example.cab302project.Activities.Activity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivityDAO {
    private final Connection connection = DatabaseConnection.getInstance();

    public List<Activity> LoadActivities() {
        String query = "SELECT activityID, activity_name, category, activity_description, goal FROM activity";
        List<Activity> activities = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                Activity activity = marshallActivity(rs);
                activities.add(activity);
            }
            return  activities;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Activity marshallActivity(ResultSet rs) throws SQLException {
        int id = rs.getInt("activityID");
        String name = rs.getString("activity_name");
        String category = rs.getString("category");
        String description = rs.getString("activity_description");
        int goal = rs.getInt("goal");
        String imageFile = null;

        return new Activity(id, name, category, description, goal, imageFile);
    }

    public Activity getActivityById(int id) {
        String query = "SELECT * FROM activity WHERE activityID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return  marshallActivity(rs);
            }else  {
                return null;
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(Activity activity) {
        int insertId = -1;

        String sql = "INSERT INTO activity" + "(activity_name, category, activity_description, goal) " + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getCategory());
            stmt.setString(3, activity.getDescription());
            stmt.setInt(4, activity.getGoal());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                    if (generatedKeys.next()) {
                        insertId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return insertId;
    }

    public Activity findByName(String name) {
        String query = "SELECT * FROM activity WHERE activity_name = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return marshallActivity(rs);
            }else  {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Activity getOrCreateActivity(Activity activity) {
        Activity existing = findByName(activity.getName());
        if (existing != null) {
            return existing;
        }
        int id = insert(activity);

        return new Activity(id, activity.getName(), activity.getCategory(), activity.getDescription(), activity.getGoal(), activity.getImageFile());
    }
}

