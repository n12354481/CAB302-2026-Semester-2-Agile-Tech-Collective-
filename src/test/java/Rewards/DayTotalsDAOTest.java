package Rewards;

import Database.DatabaseSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * In memory database, so no .db file is written when built.
 */
class DayTotalsDAOTest {

    private static final int USER = 1;
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
                    + "VALUES (1, 'a@qut.edu.au', 'a', 'x')");
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
    void severalEntriesOnOneDayAreSummed() throws SQLException {
        logActivity(USER, MONDAY, 20);
        logActivity(USER, MONDAY, 40);
        logRest(USER, MONDAY, "SLEEP", 420);
        logRest(USER, MONDAY, "DELIBERATE", 30);

        DayTotals monday = dao.forRange(USER, MONDAY, MONDAY).get(0);
        assertEquals(60, monday.activityMinutes());
        assertEquals(450, monday.restMinutes());
    }
}
