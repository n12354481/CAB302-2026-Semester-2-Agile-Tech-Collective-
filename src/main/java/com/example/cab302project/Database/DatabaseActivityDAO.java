package com.example.cab302project.Database;

import com.example.cab302project.Activities.Activity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * handles database operations related to activities
 *
 * used to insert activities, retrieve saved activities and check whether
 * an activity already exists in database
 */

public class DatabaseActivityDAO {
    private final Connection connection = DatabaseConnection.getInstance();

    // converts a database result into an activity object
    private static Activity marshallActivity(ResultSet rs) throws SQLException {
        int id = rs.getInt("activityID");
        String name = rs.getString("activity_name");
        String category = rs.getString("category");
        String description = rs.getString("activity_description");
        int goal = rs.getInt("goal");
        String imageFile = null; // not stored yet

        return new Activity(id, name, category, description, goal, imageFile);
    }

    // finds activity using database id
    public Activity getActivityById(int id) {
        String query = "SELECT * FROM activity WHERE activityID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) return marshallActivity(rs);
            return null;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * inserts a new activity into db, returns the generated or -1 on failure
     */
    public int insert(Activity activity) {
        int insertId = -1;

        String sql = "INSERT INTO activity" + "(activity_name, category, activity_description, goal) " + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getCategory());
            stmt.setString(3, activity.getDescription());
            stmt.setInt(4, activity.getGoal());

            int affectedRows = stmt.executeUpdate();

            // retrieves id created by database
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

    /**
     * searches for activity using its name
     *
     * @param name activity name to search for
     * @return matching activity or null if it does not exist
     */

    public Activity findByName(String name) {
        String query = "SELECT * FROM activity WHERE activity_name = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return marshallActivity(rs);
            }
                return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // returns existing activity or inserts new one if it doesn't exist yet
    public Activity getOrCreateActivity(Activity activity) {
        Activity existing = findByName(activity.getName());
        if (existing != null) return existing;

        int id = insert(activity);

        return new Activity(id, activity.getName(), activity.getCategory(), activity.getDescription(), activity.getGoal(), activity.getImageFile());
    }

    public List<Activity> getAllActivities() {
        List<Activity> savedActivities = new ArrayList<>();

        String query = "SELECT * FROM activity ORDER BY activityID";

        try (PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Activity activity = marshallActivity(rs);

                savedActivities.add(activity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return savedActivities;
    }
}

