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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SocialPostingsTest {

    private Connection connection;
    private SocialPostingsDAO socialPostingsDAO;

    @BeforeEach
    public void setUp() throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        // create post table
        String postSql = """
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
                    event_location TEXT,
                    tags TEXT
                )
                """;

        connection.createStatement().executeUpdate(postSql);

        // create registrations table
        String registrationSql = """
                CREATE TABLE registrations (
                    userId INTEGER NOT NULL,
                    postId INTEGER NOT NULL,
                    PRIMARY KEY (userId, postId)
                )
                """;

        connection.createStatement().executeUpdate(registrationSql);

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
                "Science Trivia Night",
                "Come test your science knowledge!",
                "A fun trivia event.",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT Gardens Point",
                "Science, Trivia"
        );

        int postId = socialPostingsDAO.createPost(post);

        assertTrue(postId > 0);
    }

    @Test
    public void testGetAllPosts() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia Night",
                "Come test your science knowledge!",
                "A fun trivia event.",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT Gardens Point",
                "Science, Trivia"
        );

        socialPostingsDAO.createPost(post);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        assertEquals(1, posts.size());
    }

    @Test
    public void testCreateMultiplePosts() throws SQLException {

        SocialPostings post1 = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia event",
                "Science questions",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        SocialPostings post2 = new SocialPostings(
                0,
                2,
                "Robotics Challenge",
                "Build robots",
                "Robotics event",
                null,
                "25/09/2026",
                "12:00",
                "17:00",
                "QUT",
                "Robotics"
        );

        socialPostingsDAO.createPost(post1);
        socialPostingsDAO.createPost(post2);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        assertEquals(2, posts.size());
    }

    @Test
    public void testPostTitleIsSaved() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia Night",
                "Trivia event",
                "Science questions",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        socialPostingsDAO.createPost(post);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        assertEquals("Science Trivia Night", posts.get(0).title());
    }

    @Test
    public void testPostDescriptionIsSaved() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Come test your science knowledge!",
                "Science questions",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        socialPostingsDAO.createPost(post);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        assertEquals(
                "Come test your science knowledge!",
                posts.get(0).description()
        );
    }

    @Test
    public void testPostEventDetailsAreSaved() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia event",
                "Science questions",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT Gardens Point",
                "Science"
        );

        socialPostingsDAO.createPost(post);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        SocialPostings savedPost = posts.get(0);

        assertEquals("20/09/2026", savedPost.eventDate());
        assertEquals("16:00", savedPost.startTime());
        assertEquals("20:00", savedPost.endTime());
        assertEquals("QUT Gardens Point", savedPost.eventLocation());
    }

    @Test
    public void testPostTagsAreSaved() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia event",
                "Science questions",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science, QUT"
        );

        socialPostingsDAO.createPost(post);

        List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

        assertEquals("Science, QUT", posts.get(0).tags());
    }

    @Test
    public void testPostsHaveDifferentIds() throws SQLException {

        SocialPostings post1 = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia",
                "Science event",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        SocialPostings post2 = new SocialPostings(
                0,
                2,
                "Robotics Challenge",
                "Robotics",
                "Build robots",
                null,
                "25/09/2026",
                "12:00",
                "17:00",
                "QUT",
                "Robotics"
        );

        int id1 = socialPostingsDAO.createPost(post1);
        int id2 = socialPostingsDAO.createPost(post2);

        assertTrue(id1 != id2);
    }

    @Test
    public void testRegisterForPost() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia",
                "Science event",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        int postId = socialPostingsDAO.createPost(post);

        socialPostingsDAO.registerForPost(1, postId);

        assertTrue(
                socialPostingsDAO.isRegistered(1, postId)
        );
    }

    @Test
    public void testUserIsNotRegisteredInitially() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia",
                "Science event",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        int postId = socialPostingsDAO.createPost(post);

        assertFalse(
                socialPostingsDAO.isRegistered(1, postId)
        );
    }

    @Test
    public void testUnregisterFromPost() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia",
                "Science event",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        int postId = socialPostingsDAO.createPost(post);

        socialPostingsDAO.registerForPost(1, postId);

        assertTrue(
                socialPostingsDAO.isRegistered(1, postId)
        );

        socialPostingsDAO.unregisterFromPost(1, postId);

        assertFalse(
                socialPostingsDAO.isRegistered(1, postId)
        );
    }

    @Test
    public void testGetRegisteredPosts() throws SQLException {

        SocialPostings post = new SocialPostings(
                0,
                1,
                "Science Trivia",
                "Trivia",
                "Science event",
                null,
                "20/09/2026",
                "16:00",
                "20:00",
                "QUT",
                "Science"
        );

        int postId = socialPostingsDAO.createPost(post);

        socialPostingsDAO.registerForPost(1, postId);

        List<SocialPostings> registeredPosts =
                socialPostingsDAO.getRegisteredPosts(1);

        assertEquals(1, registeredPosts.size());
        assertEquals("Science Trivia", registeredPosts.get(0).title());
    }
}