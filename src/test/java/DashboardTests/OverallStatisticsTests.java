package DashboardTests;

import com.example.cab302project.Dashboard.DashboardModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class is the test class for the Dashboard feature.
 */
public class OverallStatisticsTests {
    private DashboardModel[] model;

    @BeforeEach
    public void setUp() {
        model = new DashboardModel[]{
                new DashboardModel(4, 6, 17.5, 8.4, 150, 300, null, null),
                new DashboardModel(1, 0, 2.5, 2, 10, 0, null, null),
        };
    }

    //A test for seeing whether the retrieved checkin weekly statistics are correct.
    @Test
    public void testGetCorrectWeeklyStats()
    {
        assertEquals(4, model[0].getWeeklyCheckinsStreak());
    }

    //A test for seeing whether the retrieved average sleep statistics are correct.
    @Test
    public void testAverageSleep()
    {
        assertEquals(8.4, model[0].getAvgSleep());
    }

    //A test for seeing whether the retrieved average study stress statistics are correct.
    @Test
    public void testAverageStudyStress()
    {
        assertEquals(17.5, model[0].getAvgStudyStress());
    }

    //A test for seeing whether the retrieved activity minutes statistics are correct.
    @Test
    public void testGetActivityMinutes()
    {
        assertEquals(150, model[0].getActivityMinutes());
    }

    //A test for seeing whether the retrieved activity goals are correct.
    @Test
    public void testActivitiesGoals()
    {
        assertEquals(300, model[0].getActivityGoal());
    }

    //A test for seeing whether the retrieved completed activities are correct.
    @Test
    public void testActivitiesCompleted()
    {
        assertEquals(6, model[0].getActivitiesCompleted());
    }

    //A test for seeing whether the retrieved progress is correct.
    @Test
    public void testCorrectProgressCalculationForMinutes()
    {
        assertEquals(0.5, (double) model[0].getActivityMinutes()/model[0].getActivityGoal());
    }

    //A test to check whether the retrieved statistics are correct for no activity goal data.
    @Test
    public void testNoActivityGoal()
    {
        assertEquals(0, model[1].getActivityGoal());
    }

    //A test to check whether the retrieved progress is correct for no activity goal data.
    @Test
    public void testAppropriateActivityProgressCalculationWithNoGoal()
    {
        double progress = model[1].getTotalActivityMinutes();
        assertEquals(10, progress);
    }
}