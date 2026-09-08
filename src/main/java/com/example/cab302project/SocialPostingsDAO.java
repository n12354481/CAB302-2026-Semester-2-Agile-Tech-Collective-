package com.example.cab302project;

import com.example.cab302project.Database.DatabaseConnection;
import com.example.cab302project.SocialPostings.ISocialPostingsDAO;

import java.sql.*;

public class SocialPostingsDAO implements ISocialPostingsDAO {

    private Connection connection;

    public SocialPostingsDAO() {
        connection = DatabaseConnection.getInstance();
        createTables();
    }

    private void createTables() {

        String posts = "CREATE TABLE IF NOT EXISTS posts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "description TEXT, "
                + "event_date TEXT, "
                + "event_time_start TEXT, "
                + "event_time_end TEXT, "
                + "location_text TEXT, "
                + "map_location TEXT, "
                + "contact_info TEXT, "
                + "register_link TEXT, "
                + "status TEXT NOT NULL DEFAULT 'draft'"
                + ")";

        String images = "CREATE TABLE IF NOT EXISTS post_images ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "post_id INTEGER NOT NULL, "
                + "image_path TEXT NOT NULL, "
                + "sort_order INTEGER DEFAULT 0, "
                + "FOREIGN KEY (post_id) REFERENCES social_posts(id) ON DELETE CASCADE"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(posts);
            statement.executeUpdate(images);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
