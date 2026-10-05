package RewardsTests;

import com.example.cab302project.Database.DatabaseSchema;
import com.example.cab302project.Rewards.ClaimsDAO;
import com.example.cab302project.Rewards.GardenSchema;
import com.example.cab302project.Rewards.Reward;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Progress comes from each user's own logs, and a claim only belongs to the user who made it. */
class ClaimsDAOTest {

    private static final int USER = 1;
    private static final int OTHER_USER = 2;
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 5);

    private Connection connection;
    private ClaimsDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseSchema.createAll(connection);
        GardenSchema.create(connection);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO users (userID, email, username, password) VALUES "
                    + "(1, 'one@qut.edu.au', 'one', 'x'), (2, 'two@qut.edu.au', 'two', 'x')");
            statement.executeUpdate("INSERT INTO activity (activityID, activity_name, category) "
                    + "VALUES (1, 'Reading', 'Others'), (2, 'Jogging', 'Fitness')");
        }
        dao = new ClaimsDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    private void log(int userID, int activityID, int times) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (int i = 0; i < times; i++) {
                statement.executeUpdate("INSERT INTO activity_log (userID, activityID, log_date, "
                        + "minutes) VALUES (" + userID + ", " + activityID + ", '2026-10-01', 30)");
            }
        }
    }

    private static Reward find(List<Reward> rewards, String key) {
        return rewards.stream().filter(reward -> reward.key().equals(key)).findFirst().orElseThrow();
    }

    @Test
    void progressCountsOnlyThatUsersLogsInTheRewardsCategory() throws SQLException {
        log(USER, 1, 3);
        log(USER, 2, 1);
        log(OTHER_USER, 1, 5);

        Reward gnome = find(dao.rewardsFor(USER), "garden-gnome");
        assertEquals("3 of 5 activities", gnome.progress());
        assertFalse(gnome.complete());
    }

    @Test
    void aNewUserStartsAtZeroOnEverything() throws SQLException {
        for (Reward reward : dao.rewardsFor(USER)) {
            assertEquals(0.0, reward.fraction());
            assertFalse(reward.claimed());
        }
    }

    @Test
    void aClaimIsSavedForThatUserOnly() throws SQLException {
        log(USER, 1, 5);
        log(OTHER_USER, 1, 5);

        dao.claim(USER, find(dao.rewardsFor(USER), "garden-gnome"), TODAY);

        Reward mine = find(dao.rewardsFor(USER), "garden-gnome");
        assertTrue(mine.claimed());
        assertEquals(TODAY, mine.claimedOn());
        assertFalse(find(dao.rewardsFor(OTHER_USER), "garden-gnome").claimed());
    }

    @Test
    void cannotClaimBeforeTheTargetIsMet() throws SQLException {
        log(USER, 1, 4);
        Reward gnome = find(dao.rewardsFor(USER), "garden-gnome");
        assertThrows(IllegalStateException.class, () -> dao.claim(USER, gnome, TODAY));
        assertFalse(find(dao.rewardsFor(USER), "garden-gnome").claimed());
    }
}
