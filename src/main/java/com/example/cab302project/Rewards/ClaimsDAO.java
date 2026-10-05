package com.example.cab302project.Rewards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The rewards on the claiming page for one user.
 *
 * Each reward counts how many times the user has logged an activity in one category
 * ({@code activity.category}). Claims are saved in {@code reward_claim}.
 */
public final class ClaimsDAO {

    /** What a reward counts and how many it needs. */
    private record Definition(String key, String name, String category, String counts, int target,
                              String description) {
    }

    /** Placeholder targets until the real ones are agreed. */
    private static final List<Definition> CATALOGUE = List.of(
            new Definition("garden-gnome", "Garden gnome", "Others", "Other activities", 5,
                    "Earned for five other activities. Added to your garden once you confirm."),
            new Definition("jacaranda", "Jacaranda", "Social", "Social activities", 10,
                    "Earned for ten social activities. Planted in your garden once you confirm."),
            new Definition("eucalyptus", "Eucalyptus", "Fitness", "Fitness activities", 15,
                    "Earned for fifteen fitness activities. Planted in your garden once you "
                            + "confirm."),
            new Definition("green-thumb", "“Green Thumb” title", "Fitness", "Fitness activities", 30,
                    "Earned for thirty fitness activities. Added to your profile once you "
                            + "confirm."));

    private static final String COUNT_BY_CATEGORY =
            "SELECT a.category, COUNT(*) AS total FROM activity_log l "
                    + "JOIN activity a ON a.activityID = l.activityID "
                    + "WHERE l.userID = ? GROUP BY a.category";

    private static final String CLAIMS =
            "SELECT reward_key, claimed_on FROM reward_claim WHERE userID = ?";

    private static final String INSERT_CLAIM =
            "INSERT INTO reward_claim (userID, reward_key, claimed_on) VALUES (?, ?, ?)";

    private final Connection connection;

    public ClaimsDAO(Connection connection) {
        this.connection = connection;
    }

    /** Every reward, with the user's progress and any claim already made. */
    public List<Reward> rewardsFor(int userID) throws SQLException {
        Map<String, Integer> counts = countsByCategory(userID);
        Map<String, LocalDate> claims = claims(userID);

        List<Reward> rewards = new ArrayList<>();
        for (Definition definition : CATALOGUE) {
            Reward reward = new Reward(definition.key(), definition.name(),
                    definition.counts(),
                    counts.getOrDefault(definition.category(), 0), definition.target(),
                    "activities", definition.description());
            LocalDate claimedOn = claims.get(definition.key());
            if (claimedOn != null) {
                reward.restoreClaim(claimedOn);
            }
            rewards.add(reward);
        }
        return rewards;
    }

    /** Marks the reward claimed and saves it. Throws if the target is not met yet. */
    public void claim(int userID, Reward reward, LocalDate date) throws SQLException {
        reward.claim(date);
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CLAIM)) {
            statement.setInt(1, userID);
            statement.setString(2, reward.key());
            statement.setString(3, date.toString());
            statement.executeUpdate();
        }
    }

    private Map<String, Integer> countsByCategory(int userID) throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(COUNT_BY_CATEGORY)) {
            statement.setInt(1, userID);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    counts.put(results.getString("category"), results.getInt("total"));
                }
            }
        }
        return counts;
    }

    private Map<String, LocalDate> claims(int userID) throws SQLException {
        Map<String, LocalDate> claims = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(CLAIMS)) {
            statement.setInt(1, userID);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    claims.put(results.getString("reward_key"),
                            LocalDate.parse(results.getString("claimed_on")));
                }
            }
        }
        return claims;
    }
}
