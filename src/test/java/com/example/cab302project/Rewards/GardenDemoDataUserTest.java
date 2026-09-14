package com.example.cab302project.Rewards;

import com.example.cab302project.Database.DatabaseSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The panel seeds into an empty database so the demo user has to appear with no sign up/login */
class GardenDemoDataUserTest {

    private static final int USER = 1;

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseSchema.createAll(connection);
        GardenSchema.create(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    private int countUsers() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet results = statement.executeQuery("SELECT COUNT(*) FROM users")) {
            results.next();
            return results.getInt(1);
        }
    }

    @Test
    void seedingWorksOnACompletelyEmptyDatabase() throws SQLException {
        GardenDemoData.ensureUser(connection, USER);
        GardenDemoData.ensureUser(connection, USER);
        assertEquals(1, countUsers(), "the demo user is created once");

        GardenDemoData.seed(connection, USER, LocalDate.of(2026, 9, 12));

        RewardsService service = new RewardsService(connection, new int[]{3, 15});
        assertEquals(9, service.summaryForLastDays(USER, 14, LocalDate.of(2026, 9, 12))
                .balancedDays());
    }
}
