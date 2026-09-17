package SocialPostingsTest;

import com.example.cab302project.Database.SocialPostingsDAO;
import com.example.cab302project.SocialPostings.SocialPostings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SocialPostingsTest {
    private Connection connection;
    private SocialPostingsDAO socialPostingsDAO;

    @BeforeEach
    public void setUp() throws SQLException {

        // tempory database that is separate from contacts.db
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        String sql = """
                CREATE TABLE post (
                postId INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                title TEXT,
                description TEXT,
                content TEXT,
                image TEXT,
                event_date TEXT,
                start_time TEXT,
                end_time TEXT,
                event_location TEXT
               )
               """;

        connection.createStatement().executeUpdate(sql);

        socialPostingsDAO = new SocialPostingsDAO(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    public void testCreatePost() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Test Social Post",
                "Test description",
                "Test content",
                "",
                "2026-12-01",
                "10:00",
                "12:00",
                "Griffith University"
        );

        int postId = socialPostingsDAO.createPost(post);

        assertTrue(postId > 0);
    }

    @Test
    public void testGetAllPosts() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Get All Posts Test",
                "Test description",
                "Test content",
                "",
                "2026-12-02",
                "13:00",
                "14:00",
                "QUT Gardens Point"
        );

        socialPostingsDAO.createPost(post);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        boolean foundPost = false;

        for (SocialPostings savedPost : posts) {
            if (savedPost.title().equals("Get All Posts Test")) {
                foundPost = true;
                break;
            }
        }

        assertTrue(foundPost);
    }

    @Test
    public void testCreateMultiplePosts() throws SQLException {

        SocialPostings postOne = new SocialPostings(
                0,
                1,
                "Test Post One",
                "First test post",
                "First test content",
                "",
                "2026-12-03",
                "14:00",
                "16:00",
                "University of Queensland, B Block"
        );

        SocialPostings postTwo = new SocialPostings(
                0,
                1,
                "Test Post Two",
                "Second test post",
                "Second test content",
                "",
                "2026-12-04",
                "12:00",
                "13:00",
                "University of Queensland, L Block"
        );

        int firstId = socialPostingsDAO.createPost(postOne);
        int secondId = socialPostingsDAO.createPost(postTwo);

        assertTrue(firstId > 0);
        assertTrue(secondId > 0);
        assertTrue(firstId != secondId);
    }

    @Test
    public void testPostDetailsAreSaved() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Details Test",
                "Test description",
                "Test content",
                "test.png",
                "2026-12-05",
                "14:00",
                "16:00",
                "QUT Kelvin Grove"
        );

        int postId = socialPostingsDAO.createPost(post);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        for (SocialPostings savedPost : posts) {
            if (savedPost.postId() == postId) {
                assertEquals("Details Test", savedPost.title());
                assertEquals("Test description", savedPost.description());
                assertEquals("Test content", savedPost.content());
                assertEquals("test.png", savedPost.image());
                assertEquals("2026-12-05", savedPost.eventDate());
                assertEquals("14:00", savedPost.startTime());
                assertEquals("16:00", savedPost.endTime());
                assertEquals("QUT Kelvin Grove", savedPost.eventLocation());
                return;
            }
        }

        assertTrue(false);
    }

    @Test
    public void testAddPostsFeed() throws SQLException {
        socialPostingsDAO.addPostsFeed();

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        boolean foundNetworkingNight = false;
        boolean foundMathWorkshop = false;
        boolean foundScienceTrivia = false;

        for (SocialPostings post : posts) {

            if (post.title().equals("STEM Networking Night")) {
                foundNetworkingNight = true;
            }

            if (post.title().equals("Math Workshop")) {
                foundMathWorkshop = true;
            }

            if (post.title().equals("Science Trivia Night!")) {
                foundScienceTrivia = true;
            }
        }

        assertTrue(foundNetworkingNight);
        assertTrue(foundMathWorkshop);
        assertTrue(foundScienceTrivia);
    }
}