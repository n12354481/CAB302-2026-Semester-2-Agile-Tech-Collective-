package Rewards;

import Database.DatabaseSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** In memory database, so no .db file is written when built. */
class RewardClaimDAOTest {

    private static final int USER = 1;
    private static final int OTHER_USER = 2;
    private static final LocalDate DAY = LocalDate.of(2026, 9, 12);

    private Connection connection;
    private RewardClaimDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseSchema.createAll(connection);
        GardenSchema.create(connection);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO users (userID, email, username, password) "
                    + "VALUES (1, 'a@qut.edu.au', 'a', 'x'), (2, 'b@qut.edu.au', 'b', 'x')");
        }
        dao = new RewardClaimDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void aClaimComesBackWithItsDay() throws SQLException {
        dao.claim(USER, "Garden gnome", DAY);
        assertEquals(Map.of("Garden gnome", DAY), dao.claimsFor(USER));
    }

    /** Like closing the app and opening it again: a real file, closed and reopened. */
    @Test
    void aClaimSurvivesClosingTheDatabase(@TempDir Path folder) throws SQLException {
        String url = "jdbc:sqlite:" + folder.resolve("claims.db");
        try (Connection first = DriverManager.getConnection(url)) {
            DatabaseSchema.createAll(first);
            GardenSchema.create(first);
            GardenDemoData.ensureUser(first, USER);
            new RewardClaimDAO(first).claim(USER, "Garden gnome", DAY);
        }
        try (Connection second = DriverManager.getConnection(url)) {
            assertEquals(Map.of("Garden gnome", DAY), new RewardClaimDAO(second).claimsFor(USER));
        }
    }

    @Test
    void anotherUsersClaimsAreNotReturned() throws SQLException {
        dao.claim(OTHER_USER, "Garden gnome", DAY);
        assertTrue(dao.claimsFor(USER).isEmpty());
    }
}
