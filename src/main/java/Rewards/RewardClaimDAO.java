package Rewards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Saves and reads which rewards a user has claimed.
 *
 * A reward can only be claimed once, so claiming it again keeps the first day.
 */
public final class RewardClaimDAO {

    private static final String INSERT =
            "INSERT OR IGNORE INTO reward_claim (userID, reward_name, claimed_on) VALUES (?, ?, ?)";

    private static final String DELETE =
            "DELETE FROM reward_claim WHERE userID = ? AND reward_name = ?";

    private static final String SELECT =
            "SELECT reward_name, claimed_on FROM reward_claim WHERE userID = ?";

    private final Connection connection;

    public RewardClaimDAO(Connection connection) {
        this.connection = connection;
    }

    public void claim(int userID, String rewardName, LocalDate date) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setInt(1, userID);
            statement.setString(2, rewardName);
            statement.setString(3, date.toString());
            statement.executeUpdate();
        }
    }

    /** Removes the claim. Nothing happens if it was never claimed. */
    public void unclaim(int userID, String rewardName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, userID);
            statement.setString(2, rewardName);
            statement.executeUpdate();
        }
    }

    /** Reward name to the day it was claimed. */
    public Map<String, LocalDate> claimsFor(int userID) throws SQLException {
        Map<String, LocalDate> claims = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT)) {
            statement.setInt(1, userID);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    claims.put(results.getString(1), LocalDate.parse(results.getString(2)));
                }
            }
        }
        return claims;
    }
}
