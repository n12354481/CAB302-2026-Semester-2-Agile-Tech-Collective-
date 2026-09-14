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
}
