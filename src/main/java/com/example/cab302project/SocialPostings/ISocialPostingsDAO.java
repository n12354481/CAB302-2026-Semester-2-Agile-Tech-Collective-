package com.example.cab302project.SocialPostings;

import java.sql.SQLException;
import java.util.List;

public interface ISocialPostingsDAO {
    /**
     * fetches all posts from the database
     */
    List<SocialPostings> getAllPosts () throws SQLException;

    /**
     * inserts a new post from the user into the database
     */
    int createPost(SocialPostings post) throws SQLException;

    // event registration

    /**
     * Registers the user for the given post
     * @param userId
     * @param postId
     * @throws SQLException
     */
    void registerForPost(int userId, int postId) throws SQLException;

    /**
     * Removes the user's registration from post
     * @param userId
     * @param postId
     * @throws SQLException
     */
    void unregisterFromPost(int userId, int postId) throws SQLException;

    /**
     * True if the user is already registered for given post
     * @param userId
     * @param postId
     * @return
     * @throws SQLException
     */
    boolean isRegistered(int userId, int postId) throws SQLException;

    /**
     * All posts that the user is registed for
     * @param userId
     * @return
     * @throws SQLException
     */
    List<SocialPostings> getRegisteredPosts(int userId) throws SQLException;

}
