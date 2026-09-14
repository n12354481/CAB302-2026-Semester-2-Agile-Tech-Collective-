package RewardsTests;

import com.example.cab302project.Database.DatabaseSchema;
import com.example.cab302project.Rewards.DayEntry;
import com.example.cab302project.Rewards.DayTotals;
import com.example.cab302project.Rewards.DayTotalsDAO;
import com.example.cab302project.Rewards.GardenSchema;
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
    void emptyDatabaseStillReturnsARowPerDayOldestFirst() throws SQLException {
        List<DayTotals> days = dao.forRange(USER, MONDAY, MONDAY.plusDays(6));
        assertEquals(7, days.size());
        assertEquals(new DayTotals(MONDAY, 0, 0), days.get(0));
        assertEquals(MONDAY.plusDays(6), days.get(6).date());
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
    void rangeEndsCountGapsAreZeroAndOutsideIsIgnored() throws SQLException {
        logActivity(USER, MONDAY.minusDays(1), 999);
        logActivity(USER, MONDAY, 15);
        logActivity(USER, MONDAY.plusDays(2), 25);
        logActivity(USER, MONDAY.plusDays(3), 999);

        List<DayTotals> days = dao.forRange(USER, MONDAY, MONDAY.plusDays(2));
        assertEquals(15, days.get(0).activityMinutes());
        assertEquals(0, days.get(1).activityMinutes(), "Tuesday had nothing logged");
        assertEquals(25, days.get(2).activityMinutes());
    }

    @Test
    void anotherUsersEntriesAreNotCounted() throws SQLException {
        logActivity(OTHER_USER, MONDAY, 120);
        logRest(OTHER_USER, MONDAY, "SLEEP", 480);

        assertEquals(new DayTotals(MONDAY, 0, 0), dao.forRange(USER, MONDAY, MONDAY).get(0));
    }

    @Test
    void entriesOnADayComeBackForThatUserAndDayOnly() throws SQLException {
        logActivity(USER, MONDAY, 20);
        logActivity(USER, MONDAY, 40);
        logActivity(USER, MONDAY.plusDays(1), 999);
        logActivity(OTHER_USER, MONDAY, 999);
        logRest(USER, MONDAY, "SLEEP", 420);
        logRest(USER, MONDAY, "DELIBERATE", 20);
        logRest(USER, MONDAY.plusDays(1), "SLEEP", 999);
        logRest(OTHER_USER, MONDAY, "SLEEP", 999);

        assertEquals(List.of(new DayEntry("Walk", "MOVEMENT", 20), new DayEntry("Walk", "MOVEMENT", 40)),
                dao.activitiesOn(USER, MONDAY));
        assertEquals(List.of(new DayEntry("test", "SLEEP", 420), new DayEntry("test", "DELIBERATE", 20)),
                dao.restOn(USER, MONDAY));
    }
}
