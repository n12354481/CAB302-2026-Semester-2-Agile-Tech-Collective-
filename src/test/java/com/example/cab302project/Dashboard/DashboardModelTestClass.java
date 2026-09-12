package com.example.cab302project.Dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class is the test class for the Dashboard feature.
 */
public class DashboardModelTestClass {
    private DashboardModel[] model;

    @BeforeEach
    public void setUp() {
        model = new DashboardModel[]{
                new DashboardModel(4, 6, 17.5, 8.4, 150, 300),
                new DashboardModel(1, 0, 2.5, 2, 10, 0),
        };
    }

    //Overall Statistics Section
    @Test
    public void testGetCorrectWeeklyStats()
    {
        assertEquals(4, model[0].getWeeklyCheckinsStreak());
    }

    @Test
    public void testAverageSleep()
    {
        assertEquals(8.4, model[0].getAvgSleep());
    }

    @Test
    public void testAverageStudyStress()
    {
        assertEquals(17.5, model[0].getAvgStudyStress());
    }

    @Test
    public void testGetActivityMinutes()
    {
        assertEquals(150, model[0].getActivityMinutes());
    }

    @Test
    public void testActivitiesGoals()
    {
        assertEquals(300, model[0].getActivityGoal());
    }

    @Test
    public void testActivitiesCompleted()
    {
        assertEquals(6, model[0].getActivitiesCompleted());
    }

    @Test
    public void testCorrectProgressCalculationForMinutes()
    {
        assertEquals(150/300, model[0].getActivityMinutes()/model[0].getActivityGoal());
    }

    @Test
    public void testNoActivityGoal()
    {
        assertEquals(0, model[1].getActivityGoal());
    }

    public void testAppropriateActivityProgressCalculationWithNoGoal()
    {
        assertEquals(10, model[1].getActivityMinutes()/model[1].getActivityGoal());
    }
}