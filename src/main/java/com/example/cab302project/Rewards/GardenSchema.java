package com.example.cab302project.Rewards;

import com.example.cab302project.Database.DatabaseSchema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Rewards tables -- the garden.
 *
 * <p>Each day is one plant: activity grows the stem above the ground line, rest grows the
 * root below it. A day that does both is balanced, and balanced days bank a growth stage.
 *
 * <p>Needs {@link DatabaseSchema} created first.
 */
public final class GardenSchema {

    private GardenSchema() {
    }

    private static final String[] TABLES = {

            // Goal history, not one current value, so a day keeps the goal it was logged
            // under. Latest effective_from on or before a date wins for that date.
            "CREATE TABLE IF NOT EXISTS garden_goal ("
                    + "goalID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "userID INTEGER NOT NULL, "
                    + "activity_goal_minutes INTEGER NOT NULL DEFAULT 60, "
                    + "rest_goal_minutes INTEGER NOT NULL DEFAULT 420, "
                    + "effective_from TEXT NOT NULL, "
                    + "UNIQUE (userID, effective_from), "
                    + "FOREIGN KEY (userID) REFERENCES users (userID) ON DELETE CASCADE)",

            // Below the ground: sleep is the single dominant root (taproot), deliberate rest the side roots.
            "CREATE TABLE IF NOT EXISTS rest_entry ("
                    + "restID INTEGER PRIMARY KEY AUTOINCREMENT,  "
                    + "userID INTEGER NOT NULL, "
                    + "entry_date TEXT NOT NULL, "
                    + "label TEXT NOT NULL, "
                    + "kind TEXT NOT NULL CHECK (kind IN ('SLEEP', 'DELIBERATE')), "
                    + "minutes INTEGER NOT NULL, "
                    + "FOREIGN KEY (userID) REFERENCES users (userID) ON DELETE CASCADE)",

            // Thresholds as data so the numbers can be set without using code.
            // Not seeded -- the stage names and counts are still unconfirmed.
            "CREATE TABLE IF NOT EXISTS growth_stage ("
                    + "stage_number INTEGER PRIMARY KEY, "
                    + "stage_name TEXT NOT NULL UNIQUE, "
                    + "balanced_days_required INTEGER NOT NULL)",

            // One row per reward a user has claimed. The key matches ClaimsDAO's catalogue.
            "CREATE TABLE IF NOT EXISTS reward_claim ("
                    + "userID INTEGER NOT NULL, "
                    + "reward_key TEXT NOT NULL, "
                    + "claimed_on TEXT NOT NULL, "
                    + "PRIMARY KEY (userID, reward_key), "
                    + "FOREIGN KEY (userID) REFERENCES users (userID) ON DELETE CASCADE)"
    };

    /** Creates the garden tables if they are not already there. Safe to call on startup. */
    public static void create(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String ddl : TABLES) {
                statement.executeUpdate(ddl);
            }
        }
    }
}
