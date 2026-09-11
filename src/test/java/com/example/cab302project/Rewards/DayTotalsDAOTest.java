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

/** In memory database, so no .db file is written when built. */
class DayTotalsDAOTest {

    private static final int USER = 1;
    private static final int OTHER_USER = 2;
    private static final LocalDate MONDAY = LocalDate.of(2026, 9, 7);

    private Connection connection;
    private DayTotalsDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseSchema.createAll(connection);
        GardenSchema.create(connection);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO users (userID, email, username, password) "
                    + "VALUES (1, 'a@qut.edu.au', 'a', 'x'), (2, 'b@qut.edu.au', 'b', 'x')");
            statement.executeUpdate("INSERT INTO activity (activityID, activity_name, category) "
                    + "VALUES (1, 'Walk', 'MOVEMENT')");
        }
        dao = new DayTotalsDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    private void logActivity(int userID, LocalDate date, int minutes) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO activity_log (userID, activityID, log_date, minutes) "
                    + "VALUES (" + userID + ", 1, '" + date + "', " + minutes + ")");
        }
    }

    private void logRest(int userID, LocalDate date, String kind, int minutes) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO rest_entry (userID, entry_date, label, kind, minutes) "
                    + "VALUES (" + userID + ", '" + date + "', 'test', '" + kind + "', " + minutes + ")");
        }
    }

    @Test
    void emptyDatabaseStillReturnsARowPerDay() throws SQLException {
        List<DayTotals> days = dao.forRange(USER, MONDAY, MONDAY.plusDays(6));
        assertEquals(7, days.size());
        assertEquals(new DayTotals(MONDAY, 0, 0), days.get(0));
    }

    @Test
    void severalEntriesOnOneDayAreSummed() throws SQLException {
        logActivity(USER, MONDAY, 20);
        logActivity(USER, MONDAY, 40);
        logRest(USER, MONDAY, "SLEEP", 420);
        logRest(USER, MONDAY, "DELIBERATE", 30);

        DayTotals monday = dao.forRange(USER, MONDAY, MONDAY).get(0);
        assertEquals(60, monday.activityMinutes());
        assertEquals(450, monday.restMinutes());
    }

    @Test
    void gapsInTheMiddleComeBackAsZeroRows() throws SQLException {
        logActivity(USER, MONDAY, 30);
        logActivity(USER, MONDAY.plusDays(2), 30);

        List<DayTotals> days = dao.forRange(USER, MONDAY, MONDAY.plusDays(2));
        assertEquals(30, days.get(0).activityMinutes());
        assertEquals(0, days.get(1).activityMinutes(), "Tuesday had nothing logged");
        assertEquals(30, days.get(2).activityMinutes());
    }

    @Test
    void daysComeBackOldestFirst() throws SQLException {
        List<DayTotals> days = dao.forRange(USER, MONDAY, MONDAY.plusDays(3));
        assertEquals(MONDAY, days.get(0).date());
        assertEquals(MONDAY.plusDays(3), days.get(3).date());
    }

    @Test
    void entriesOutsideTheRangeAreIgnored() throws SQLException {
        logActivity(USER, MONDAY.minusDays(1), 999);
        logActivity(USER, MONDAY.plusDays(1), 999);

        assertEquals(0, dao.forRange(USER, MONDAY, MONDAY).get(0).activityMinutes());
    }

    @Test
    void theRangeEndsAreIncluded() throws SQLException {
        logActivity(USER, MONDAY, 15);
        logActivity(USER, MONDAY.plusDays(2), 25);

        List<DayTotals> days = dao.forRange(USER, MONDAY, MONDAY.plusDays(2));
        assertEquals(15, days.get(0).activityMinutes());
        assertEquals(25, days.get(2).activityMinutes());
    }

    @Test
    void anotherUsersEntriesAreNotCounted() throws SQLException {
        logActivity(OTHER_USER, MONDAY, 120);
        logRest(OTHER_USER, MONDAY, "SLEEP", 480);

        assertEquals(new DayTotals(MONDAY, 0, 0), dao.forRange(USER, MONDAY, MONDAY).get(0));
    }

    @Test
    void aBackwardsRangeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.forRange(USER, MONDAY, MONDAY.minusDays(1)));
    }

    @Test
    void totalsFeedStraightIntoTheSummary() throws SQLException {
        logActivity(USER, MONDAY, 60);
        logRest(USER, MONDAY, "SLEEP", 420);
        logActivity(USER, MONDAY.plusDays(1), 60);
        logRest(USER, MONDAY.plusDays(1), "SLEEP", 420);
        // Day three, nothing logged which breaks the streak.

        List<DayState> states = dao.forRange(USER, MONDAY, MONDAY.plusDays(2)).stream()
                .map(day -> day.state(60, 420))
                .toList();

        RewardsSummary summary = RewardsSummary.of(states, new int[]{3, 15});
        assertEquals(2, summary.balancedDays());
        assertEquals(0, summary.currentStreak());
        assertEquals(0, summary.stageReached());
    }
}
