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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The seeded fortnight has to be worth demoing, so these tests pin down what it shows. */
class GardenDemoDataTest {

    private static final int USER = 1;
    private static final LocalDate LAST_DAY = LocalDate.of(2026, 9, 12);
    private static final int ACTIVITY_GOAL = 60;
    private static final int REST_GOAL = 420;

    private Connection connection;
    private DayTotalsDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseSchema.createAll(connection);
        GardenSchema.create(connection);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO users (userID, email, username, password) "
                    + "VALUES (1, 'demo@qut.edu.au', 'demo', 'x')");
        }
        dao = new DayTotalsDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    private List<DayState> seedAndClassify() throws SQLException {
        LocalDate firstDay = GardenDemoData.seed(connection, USER, LAST_DAY);
        return dao.forRange(USER, firstDay, LAST_DAY).stream()
                .map(day -> day.state(ACTIVITY_GOAL, REST_GOAL))
                .toList();
    }

    @Test
    void seedsAFortnightEndingOnTheDayGiven() throws SQLException {
        LocalDate firstDay = GardenDemoData.seed(connection, USER, LAST_DAY);
        assertEquals(LAST_DAY.minusDays(13), firstDay);
        assertEquals(GardenDemoData.DAYS, dao.forRange(USER, firstDay, LAST_DAY).size());
    }

    @Test
    void everyDayStateAppearsAtLeastOnce() throws SQLException {
        Set<DayState> seen = EnumSet.copyOf(seedAndClassify());
        assertEquals(EnumSet.allOf(DayState.class), seen,
                "the demo should show off every kind of day");
    }

    @Test
    void theSummaryLandsMidwayBetweenSproutAndFlower() throws SQLException {
        RewardsSummary summary = RewardsSummary.of(seedAndClassify(), new int[]{3, 15});
        assertEquals(9, summary.balancedDays());
        assertEquals(9, summary.currentStreak(), "unbroken since the empty day");
        assertEquals(1, summary.stageReached(), "sprouted, not yet flowered");
        assertTrue(summary.balancedDays() < 15, "leaves something to work toward on screen");
    }

    @Test
    void seedingTwiceDoesNotDoubleTheMinutes() throws SQLException {
        GardenDemoData.seed(connection, USER, LAST_DAY);
        List<DayTotals> once = dao.forRange(USER, LAST_DAY.minusDays(13), LAST_DAY);

        GardenDemoData.seed(connection, USER, LAST_DAY);
        List<DayTotals> twice = dao.forRange(USER, LAST_DAY.minusDays(13), LAST_DAY);

        assertEquals(once, twice);
    }

    @Test
    void theDemoActivityIsReusedRatherThanDuplicated() throws SQLException {
        GardenDemoData.seed(connection, USER, LAST_DAY);
        GardenDemoData.seed(connection, USER, LAST_DAY);

        try (Statement statement = connection.createStatement();
             var results = statement.executeQuery("SELECT COUNT(*) FROM activity")) {
            results.next();
            assertEquals(1, results.getInt(1));
        }
    }
}
