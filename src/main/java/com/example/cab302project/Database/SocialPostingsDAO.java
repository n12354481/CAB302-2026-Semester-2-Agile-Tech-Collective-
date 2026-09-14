package com.example.cab302project.SocialPostings;

import com.example.cab302project.Database.DatabaseConnection;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * uses the databaseconnection to query the posts table
 */
public class SocialPostingsDAO implements ISocialPostingsDAO {

    @Override
    public List<SocialPostings> getAllPosts() throws SQLException {
        List<SocialPostings> posts = new ArrayList<>();

        String sql = "SELECT postId, userId, title, description, content, image, " + "event_date, start_time, end_time, event_location " + "FROM post ORDER BY postId DESC";

        Connection connection = DatabaseConnection.getInstance();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                posts.add(new SocialPostings(
                        resultSet.getInt("postId"),
                        resultSet.getInt("userId"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("content"),
                        resultSet.getString("image"),
                        resultSet.getString("event_date"),
                        resultSet.getString("start_time"),
                        resultSet.getString("end_time"),
                        resultSet.getString("event_location")
                ));
            }
        }

        return posts;
    }

    /**
     * inserts a new post into the database
     */
    @Override
    public int createPost(SocialPostings post) throws SQLException {
        String sql = "INSERT INTO post (userId, title, description, content, image, " + "event_date, start_time, end_time, event_location) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance();

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, post.userId());
            statement.setString(2, post.title());
            statement.setString(3, post.description());
            statement.setString(4, post.content());
            statement.setString(5, post.image());
            statement.setString(6, post.eventDate());
            statement.setString(7, post.startTime());
            statement.setString(8, post.endTime());
            statement.setString(9, post.eventLocation());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        return -1;
    }

}