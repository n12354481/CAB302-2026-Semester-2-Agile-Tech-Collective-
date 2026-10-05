package DashboardTests;

import com.example.cab302project.Dashboard.CommunityInsights.CommunityInsightsData;
import com.example.cab302project.Dashboard.CommunityInsights.CommunityService;
import com.example.cab302project.Dashboard.CommunityInsights.ICommunityInsightsDAO;
import com.example.cab302project.Database.DatabaseCommunityInsightsDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the community insights section in the dashboard.
 */
public class CommunityInsightsTests {
    private CommunityInsightsData[] model;
    private ICommunityInsightsDAO dao;

    @BeforeEach
    public void setUp() {

        dao = new DatabaseCommunityInsightsDAO();
        model = new CommunityInsightsData[]{
                new CommunityInsightsData(4, 5.4, 17.5, 1, 83.4, 300, "Walking", "Stressed"),
        };

    }


    //A test to check whether the model returns the appropriate number of users participating in the community insights section.
    @Test
    void testGetParticipatingUser() {
        assertNotNull(model[0].getParticipatingUsers());
        assertEquals(4, model[0].getParticipatingUsers());
    }

    //A test to check whether the model returns the appropriate number of activity minutes for the participating users.
    @Test
    void testGetActivityMinutes() {
        assertNotNull(model[0].getTotalActivityMinutes());
        assertEquals(300, model[0].getTotalActivityMinutes());
    }

    //A test to check whether the model returns the appropriate value of average sleep for the participating users.
    @Test
    void testGetAvgSleep() {
        assertNotNull(model[0].getAvgSleep());
        assertEquals(5.4, model[0].getAvgSleep());
    }

    //A test to check whether the model returns the appropriate value of average study stress for the participating users.
    @Test
    void testGetAvgStudyStress() {
        assertNotNull(model[0].getAvgStudyStress());
        assertEquals(17.5, model[0].getAvgStudyStress());
    }

    //A test to check whether the model returns the appropriate value of average emotions for the participating users.
    @Test
    void testGetAvgEmotion() {
        assertNotNull(model[0].getAvgEmotion());
        assertEquals(1, model[0].getAvgEmotion());
    }

    //A test to check whether the model returns the appropriate value of average water for the participating users.
    @Test
    void testGetAvgWater() {
        assertNotNull(model[0].getAvgWater());
        assertEquals(83.4, model[0].getAvgWater());
    }

    //A test to check whether the model returns the appropriate value fo the most popular activity for the participating users.
    @Test
    void testPopularActivity() {
        assertNotNull(model[0].getMostPopularActivity());
        assertEquals("Walking", model[0].getMostPopularActivity());
    }

    //A test to check whether the model returns the appropriate value fo the most popular mood for the participating users.
    @Test
    void testPopularMood() {
        assertNotNull(model[0].getMostPopularMood());
        assertEquals("Stressed", model[0].getMostPopularMood());
    }

    //A test to check whether the service returning Ollama's response returns appropriate insights needed.
    @Test
    void testInsightsService() throws InterruptedException {
        CommunityService service = new CommunityService();


        CommunityInsightsData data = new CommunityInsightsData(
                model[0].getParticipatingUsers(),
                model[0].getAvgSleep(),
                model[0].getAvgStudyStress(),
                model[0].getAvgEmotion(),
                model[0].getAvgWater(),
                model[0].getTotalActivityMinutes(),
                model[0].getMostPopularActivity(),
                model[0].getMostPopularMood()
        );

        //To ensure that Ollama has enoguh time to respond before the test is finished.
        CountDownLatch latch = new CountDownLatch(1);

        service.generateInsights(data, response -> {
            assertNotNull(response);
            assertNotNull(response.getResponse());
            System.out.println("Recommendations:");
            System.out.println(response.getResponse());
            latch.countDown();
        });

        //Ensuring that the test did wait for 30 sec.
        assertTrue(latch.await(30, TimeUnit.SECONDS), "Ollama did not respond in 30 sec.");
    }
}
