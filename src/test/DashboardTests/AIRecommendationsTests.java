package DashboardTests;

import com.example.cab302project.Dashboard.Ollama.Connection;
import com.example.cab302project.Dashboard.Ollama.Response;
import com.example.cab302project.Dashboard.Recommendations.IRecommendationsDAO;
import com.example.cab302project.Dashboard.Recommendations.RecommendationData;
import com.example.cab302project.Dashboard.Recommendations.RecommendationService;
import com.example.cab302project.Database.DatabaseRecommendationsDAO;
import com.example.cab302project.MoodForm.CheckIn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


public class AIRecommendationsTests {
    private RecommendationData modelRecommendations;
    private IRecommendationsDAO dao;

    @BeforeEach
    public void setUp() {
        List<Map<String, Object>> activityData = new ArrayList<>();
        Map<String, Object> walking = new HashMap<>();
        walking.put("name", "Walking");
        walking.put("minutes", 30);

        activityData.add(walking);

        List<CheckIn> checkinData = new ArrayList<>();
        checkinData.add(new CheckIn(
                1, 1, LocalDate.now(), 8, 7, 6, 3, List.of("Happy, Calm")
        ));

        modelRecommendations = new RecommendationData(activityData, checkinData);

         dao = new DatabaseRecommendationsDAO();
    }


    /*
        DAO tests
    */
    //A test to check wehther the model appropriately returns recent user activity data for creating recommendations
    @Test
    void testGetActivityDataForUser() {
        assertNotNull(modelRecommendations.getActivityData());
        assertEquals(1, modelRecommendations.getActivityData().size());
    }

    //A test to check whether the model appropriately returns recent user checkin data for creating recommendations
    @Test
    void testGetCheckinDataForUser() {
        assertNotNull(modelRecommendations.getCheckinData());
        assertEquals(1, modelRecommendations.getCheckinData().size());
    }

    @Test
    void testActivityData() {
        Map<String, Object> activity = modelRecommendations.getActivityData().get(0);

        assertEquals("Walking", activity.get("name"));
        assertEquals(30, activity.get("minutes"));
    }

    //A test to check whether the model appropriately returns recent user checkin data for creating recommendations
    @Test
    void testCheckinData() {
        CheckIn checkin = modelRecommendations.getCheckinData().get(0);

        assertEquals(8, checkin.getEmotionToday());
        assertEquals(7, checkin.getSleep());
        assertEquals(6, checkin.getWater());
        assertEquals(3, checkin.getStudyStress());
    }

    /*
    Ollama tests
    */
    @Test
    void testOllamaConnection() {
        Connection connection = new Connection("http://localhost:11434/api/generate");
        connection.fetchAsynchronousOllamaResponse("llama3.2", "Give me one short wellbeing recommendation.", response -> {
            assertNotNull(response);
            assertNotNull(response.getResponse());
            System.out.println(response.getResponse());
        });
    }

    @Test
    void testRecommendationService() throws InterruptedException {
        RecommendationService service = new RecommendationService();

        List<Map<String, Object>> activityData = new ArrayList<>();
        Map<String, Object> walking = new HashMap<>();

        walking.put("name", "Walking");
        walking.put("minutes", 30);

        activityData.add(walking);

        List<CheckIn> checkinData = new ArrayList<>();

        checkinData.add(new CheckIn(1,8, LocalDate.now(), 6, 5, 4, 8, List.of("Anxious")));

        RecommendationData data = new RecommendationData(activityData, checkinData);

        CountDownLatch latch = new CountDownLatch(1);
        service.generateRecommendations(data, response -> {
            assertNotNull(response);
            assertNotNull(response.getResponse());
            System.out.println("Recommendations:");
            System.out.println(response.getResponse());
            latch.countDown();
        });

        assertTrue(latch.await(30, TimeUnit.SECONDS), "Ollama did not respond in 30 sec.");

    }
}
