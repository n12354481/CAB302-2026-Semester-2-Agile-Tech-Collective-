package SocialPostings;

import Database.DatabaseConnection;

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

}