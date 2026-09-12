package com.example.cab302project.SocialPostings;

import java.sql.SQLException;
import java.util.List;

public interface ISocialPostingsDAO {
    /**
     * fetches all posts from the database
     */
    List<SocialPostings> getAllPosts () throws SQLException;
}
