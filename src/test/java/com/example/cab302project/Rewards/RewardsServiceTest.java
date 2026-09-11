package com.example.cab302project.Rewards;

import com.example.cab302project.Database.DatabaseSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The whole chain, from seeded rows to the numbers the panel will show. */
class RewardsServiceTest {

    private static final int USER = 1;
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 12);
    private static final int[] THRESHOLDS = {3, 15};

    private Connection connection;
    private RewardsService service;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseSchema.createAll(connection);
        GardenSchema.create(connection);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO users (userID, email, username, password) "
                    + "VALUES (1, 'demo@qut.edu.au', 'demo', 'x')");
        }
        service = new RewardsService(connection, THRESHOLDS);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void aUserWithNothingLoggedGetsAllZeroes() throws SQLException {
        RewardsSummary summary = service.summaryForLastDays(USER, 14, TODAY);
        assertEquals(new RewardsSummary(0, 0, 0), summary);
    }

    @Test
    void theSeededFortnightReadsBackThroughTheService() throws SQLException {
        GardenDemoData.seed(connection, USER, TODAY);

        RewardsSummary summary = service.summaryForLastDays(USER, GardenDemoData.DAYS, TODAY);
        assertEquals(9, summary.balancedDays());
        assertEquals(9, summary.currentStreak());
        assertEquals(1, summary.stageReached());
        assertEquals(1, summary.unclaimedStages(0), "one stage owed, nothing claimed yet");
    }

    @Test
    void statesComeBackOnePerDayForDrawing() throws SQLException {
        GardenDemoData.seed(connection, USER, TODAY);

        List<DayState> states = service.statesFor(USER, TODAY.minusDays(13), TODAY,
                RewardsService.DEFAULT_ACTIVITY_GOAL, RewardsService.DEFAULT_REST_GOAL);
        assertEquals(14, states.size());
        assertEquals(DayState.BALANCED, states.get(0));
    }

    @Test
    void harderGoalsBankFewerDays() throws SQLException {
        GardenDemoData.seed(connection, USER, TODAY);

        RewardsSummary easy = service.summaryFor(USER, TODAY.minusDays(13), TODAY, 60, 420);
        RewardsSummary hard = service.summaryFor(USER, TODAY.minusDays(13), TODAY, 120, 480);
        assertEquals(9, easy.balancedDays());
        // Only the 240 minute day clears 120 minutes and 8 hours of rest.
        assertEquals(1, hard.balancedDays());
    }

    @Test
    void aShorterWindowSeesFewerDays() throws SQLException {
        GardenDemoData.seed(connection, USER, TODAY);

        // The last 3 seeded days are rested-]but-under-goal, balanced, balanced.
        assertEquals(2, service.summaryForLastDays(USER, 3, TODAY).balancedDays());
        assertEquals(9, service.summaryForLastDays(USER, 14, TODAY).balancedDays());
    }

    @Test
    void changingTheThresholdsAfterwardsDoesNotAffectTheService() throws SQLException {
        int[] mutable = {3, 15};
        RewardsService ownService = new RewardsService(connection, mutable);
        GardenDemoData.seed(connection, USER, TODAY);

        mutable[0] = 99;
        assertEquals(1, ownService.summaryForLastDays(USER, 14, TODAY).stageReached());
    }

    @Test
    void badArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.summaryForLastDays(USER, 0, TODAY));
        assertThrows(IllegalArgumentException.class,
                () -> new RewardsService(connection, null));
    }
}
