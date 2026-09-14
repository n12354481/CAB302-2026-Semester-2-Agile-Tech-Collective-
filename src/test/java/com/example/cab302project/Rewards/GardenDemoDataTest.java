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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void seedsAFortnightShowingEveryDayState() throws SQLException {
        LocalDate firstDay = GardenDemoData.seed(connection, USER, LAST_DAY);
        assertEquals(LAST_DAY.minusDays(13), firstDay);

        List<DayState> states = dao.forRange(USER, firstDay, LAST_DAY).stream()
                .map(day -> day.state(ACTIVITY_GOAL, REST_GOAL))
                .toList();
        assertEquals(GardenDemoData.DAYS, states.size());
        assertEquals(EnumSet.allOf(DayState.class), EnumSet.copyOf(states),
                "the demo should show off every kind of day");
    }

    @Test
    void seedingTwiceDoesNotDoubleAnything() throws SQLException {
        GardenDemoData.seed(connection, USER, LAST_DAY);
        List<DayTotals> once = dao.forRange(USER, LAST_DAY.minusDays(13), LAST_DAY);

        GardenDemoData.seed(connection, USER, LAST_DAY);
        List<DayTotals> twice = dao.forRange(USER, LAST_DAY.minusDays(13), LAST_DAY);

        assertEquals(once, twice);
        try (Statement statement = connection.createStatement();
             var results = statement.executeQuery("SELECT COUNT(*) FROM activity")) {
            results.next();
            assertEquals(1, results.getInt(1), "the demo activity is reused, not duplicated");
        }
    }
}
