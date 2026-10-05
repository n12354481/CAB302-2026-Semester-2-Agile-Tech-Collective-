package com.example.cab302project.Database;

import com.example.cab302project.SocialPostings.SocialPostings;
import com.example.cab302project.SocialPostings.ISocialPostingsDAO;

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

    private Connection connection;

    public SocialPostingsDAO() {
        this.connection = DatabaseConnection.getInstance();
    }

    public SocialPostingsDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<SocialPostings> getAllPosts() throws SQLException {
        List<SocialPostings> posts = new ArrayList<>();

        String sql = "SELECT postId, userId, title, description, content, image, "
                + "event_date, start_time, end_time, event_location, tags "
                + "FROM post ORDER BY postId DESC";

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
                        resultSet.getString("event_location"),
                        resultSet.getString("tags")
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
        String sql = "INSERT INTO post (userID, title, description, content, image, "
                + "event_date, start_time, end_time, event_location, tags) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            statement.setString(10, post.tags());

           statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        return -1;
    }

    /**
     * add posts into the database
     */
    public void addPostsFeed() throws SQLException {
        String sql = "INSERT INTO post "
                + "(userID, title, description, content, image, event_date, "
                + "start_time, end_time, event_location, tags) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        addPostIfNotExists(
                connection,
                sql,
                1,
                "STEM Networking Night",
                "Meet other STEM students and make new connections.",
                "Come along to our STEM networking night!",
                "",
                "2026-11-12",
                "17:00",
                "19:00",
                "QUT Gardens Point",
                "Brisbane"
        );

        addPostIfNotExists(
                connection,
                sql,
                1,
                "Math Workshop",
                "Get help with your math questions!",
                "Bring any questions, worksheets, or assignments along for extra help!",
                "",
                "2026-11-02",
                "13:00",
                "16:00",
                "QUT Gardens Point, V Block, Level 3",
                "Math, Brisbane"
        );

        addPostIfNotExists(
                connection,
                sql,
                1,
                "Science Trivia Night!",
                "Test your science knowledge against other teams and science students.",
                "Join us for a fun night of science trivia! Come alone and meet new people or come in a group and put your brains to the test!",
                "",
                "2026-10-17",
                "17:00",
                "21:00",
                "University of Queensland, Science Block, Room 413B",
                "Science"
        );
    }

    /**
     * Registers a user for a post
     * @param userId
     * @param postId
     * @throws SQLException
     */
    @Override
    public void registerForPost(int userId, int postId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO registrations (userID, postID) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);
            statement.executeUpdate();
        }
    }

    /**
     * Removes a user's registration from a post
     * @param userId
     * @param postId
     * @throws SQLException
     */
    @Override
   public void unregisterFromPost(int userId, int postId) throws SQLException {
        String sql = "DELETE FROM registrations WHERE userID = ? AND postID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);
            statement.executeUpdate();
        }
    }

    @Override
    public boolean isRegistered(int userId, int postId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations WHERE userId = ? AND postId = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    /**
     * All the posts the user has registered for
     * @param userId
     * @return
     * @throws SQLException
     */
    @Override
    public List<SocialPostings> getRegisteredPosts(int userId) throws SQLException {
        List<SocialPostings> posts = new ArrayList<>();

        String sql = "SELECT p.postId, p.userId, p.title, p.description, p.content, p.image, "
                + "p.event_date, p.start_time, p.end_time, p.event_location, p.tags "
                + "FROM post p "
                + "JOIN registrations r ON r.postID = p.postId "
                + "WHERE r.userID = ? "
                + "ORDER BY p.event_date ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
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
                            resultSet.getString("event_location"),
                            resultSet.getString("tags")
                    ));
                }
            }
        }

        return posts;
    }

    /**
     * adds a post only if a post with the same title and date does not exist
     */
    private void addPostIfNotExists(
            Connection connection,
            String sql,
            int userId,
            String title,
            String description,
            String content,
            String image,
            String eventDate,
            String startTime,
            String endTime,
            String eventLocation,
            String tags
    ) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM post "
                + "WHERE title = ? AND event_date = ?";

        try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {

            checkStatement.setString(1, title);
            checkStatement.setString(2, eventDate);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return;
                }
            }
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setString(4, content);
            statement.setString(5, image);
            statement.setString(6, eventDate);
            statement.setString(7, startTime);
            statement.setString(8, endTime);
            statement.setString(9, eventLocation);
            statement.setString(10, tags);

            statement.executeUpdate();
        }
    }
}