package com.example.cab302project.Database;

import com.example.cab302project.Activities.Activity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivityDAO {
    private final Connection connection = DatabaseConnection.getInstance();

    public List<Activity> LoadActivities() {
        String query = "SELECT id, name, category, description, goal, points, image_file FROM activity";
        List<Activity> activities = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String category = rs.getString("category");
                String description = rs.getString("description");
                int goal = rs.getInt("goal");
                String imageFile = rs.getString("image_file");

                Activity ac = new Activity(id, name, category, description, goal, imageFile);
                activities.add(ac);
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

