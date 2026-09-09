package com.example.cab302project.Rewards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Demo data for the garden, a fortnight of logged days for a user.
 *
 * Nothing writes to {@code activity_log} or {@code rest_entry} yet, there is no logging
 * screen. Without rows the rewards panel shows empty soil on and on, so this
 * enables a fixed fortnight of activities: a balanced day, a
 * rest only day, an empty day that breaks the streak, and one day that's over the activity cap.
 *
 *
 * Deterministic and replayable: seeding the same range twice clears the range first.
 * The totals do not double up.
 */
public final class GardenDemoData {

    private GardenDemoData() {
    }

    /** How many days the seeded run covers, ending on the day just passed in. */
    public static final int DAYS = 14;

    /**
     * Activity minutes per day, oldest first. Against the default 60 minute goal:
     * index 4 is empty, index 9 is over the 180 minute cap, index 6 is rest only.
     */
    private static final int[] ACTIVITY = {60, 75, 60, 30, 0, 90, 0, 60, 60, 240, 60, 45, 60, 80};

    /** Rest minutes per day, oldest first, against the default 420 minute (7 hr) goal. */
    private static final int[] REST = {420, 430, 450, 400, 0, 300, 480, 420, 440, 480, 420, 460, 425, 430};

    /**
     * Seeds {@link #DAYS} days of activity and rest for one user, ending on {@code lastDay}.
     * The user must already exist.
     *
     * @return the first date seeded
     */
    public static LocalDate seed(Connection connection, int userID, LocalDate lastDay)
            throws SQLException {
        if (lastDay == null) {
            throw new IllegalArgumentException("lastDay is required");
        }

        LocalDate firstDay = lastDay.minusDays(DAYS - 1L);
        int activityID = demoActivityID(connection);

        clearRange(connection, "activity_log", "log_date", userID, firstDay, lastDay);
        clearRange(connection, "rest_entry", "entry_date", userID, firstDay, lastDay);

        String insertActivity = "INSERT INTO activity_log (userID, activityID, log_date, minutes) "
                + "VALUES (?, ?, ?, ?)";
        String insertRest = "INSERT INTO rest_entry (userID, entry_date, label, kind, minutes) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement activity = connection.prepareStatement(insertActivity);
             PreparedStatement rest = connection.prepareStatement(insertRest)) {

            for (int i = 0; i < DAYS; i++) {
                String date = firstDay.plusDays(i).toString();

                if (ACTIVITY[i] > 0) {
                    activity.setInt(1, userID);
                    activity.setInt(2, activityID);
                    activity.setString(3, date);
                    activity.setInt(4, ACTIVITY[i]);
                    activity.executeUpdate();
                }

                if (REST[i] > 0) {
                    rest.setInt(1, userID);
                    rest.setString(2, date);
                    rest.setString(3, "Sleep");
                    rest.setString(4, "SLEEP");
                    rest.setInt(5, REST[i]);
                    rest.executeUpdate();
                }
            }
        }
        return firstDay;
    }

    /** Finds the demo activity, inserting it the first time. */
    private static int demoActivityID(Connection connection) throws SQLException {
        String name = "Walk";
        try (PreparedStatement select =
                     connection.prepareStatement("SELECT activityID FROM activity WHERE activity_name = ?")) {
            select.setString(1, name);
            try (ResultSet found = select.executeQuery()) {
                if (found.next()) {
                    return found.getInt(1);
                }
            }
        }
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO activity (activity_name, category) VALUES (?, 'MOVEMENT')",
                Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, name);
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private static void clearRange(Connection connection, String table, String dateColumn,
                                   int userID, LocalDate from, LocalDate to) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE userID = ? AND "
                + dateColumn + " BETWEEN ? AND ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userID);
            statement.setString(2, from.toString());
            statement.setString(3, to.toString());
            statement.executeUpdate();
        }
    }
}
