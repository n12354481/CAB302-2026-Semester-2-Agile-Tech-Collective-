// copied and pasted from 6.3 workshop
import com.example.cab302project.SocialPostings.SocialPostings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocialPostingsTest {

    private SocialPostings post;

    @BeforeEach
    public void setUp() {
        post = new SocialPostings(
                1,
                2,
                "Brisbane Monthly Hackathon",
                "Join us for a monthly hackathon!",
                "Full event details go here.",
                "hackathon.png",
                "2026-10-05",
                "10:00",
                "16:00",
                "University of Queensland"
        );
    }

    @Test
    public void testGetPostId() {
        assertEquals(1, post.postId());
    }

    @Test
    public void testGetUserId() {
        assertEquals(2, post.userId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("Brisbane Monthly Hackathon", post.title());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Join us for a monthly hackathon!", post.description());
    }

    @Test
    public void testGetContent() {
        assertEquals("Full event details go here.", post.content());
    }

    @Test
    public void testGetImage() {
        assertEquals("hackathon.png", post.image());
    }

    @Test
    public void testGetEventDate() {
        assertEquals("2026-10-05", post.eventDate());
    }

    @Test
    public void testGetStartTime() {
        assertEquals("10:00", post.startTime());
    }

    @Test
    public void testGetEndTime() {
        assertEquals("16:00", post.endTime());
    }

    @Test
    public void testGetEventLocation() {
        assertEquals("University of Queensland", post.eventLocation());
    }
}
