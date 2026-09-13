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
 * Reads the minutes logged per day for one user.
 *
 * Two sums (one per table), because activity and rest live in different tables with
 * different date columns. They are merged here into one row per date.
 * A date with nothing logged comes back as a zero row rather than being
 * skipped.
 *
 * Dates are ISO text in SQLite, which sorts and compares, so the range
 * comparison is BETWEEN on strings.
 *
 * Also lists the entries behind one day for the selected day.
 */
public final class DayTotalsDAO {

    private static final String ACTIVITY_SUM =
            "SELECT log_date, SUM(minutes) AS total FROM activity_log "
                    + "WHERE userID = ? AND log_date BETWEEN ? AND ? GROUP BY log_date";

    private static final String REST_SUM =
            "SELECT entry_date, SUM(minutes) AS total FROM rest_entry "
                    + "WHERE userID = ? AND entry_date BETWEEN ? AND ? GROUP BY entry_date";

    private static final String ACTIVITY_ENTRIES =
            "SELECT a.activity_name, a.category, l.minutes FROM activity_log l "
                    + "JOIN activity a ON a.activityID = l.activityID "
                    + "WHERE l.userID = ? AND l.log_date = ? ORDER BY l.rowid";

    private static final String REST_ENTRIES =
            "SELECT label, kind, minutes FROM rest_entry "
                    + "WHERE userID = ? AND entry_date = ? ORDER BY restID";

    private final Connection connection;

    public DayTotalsDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Every date from {@code from} to {@code to} inclusive, oldest first, with the minutes
     * logged on each. Ready to give to {@link RewardsSummary#of} once each row is classified.
     *
     * @throws IllegalArgumentException if {@code to} is before {@code from}
     */
    public List<DayTotals> forRange(int userID, LocalDate from, LocalDate to) throws SQLException {
        if (from == null || to == null) {
            throw new IllegalArgumentException("both dates are required");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("to (" + to + ") is before from (" + from + ")");
        }

        Map<LocalDate, Integer> activity = sumByDate(ACTIVITY_SUM, userID, from, to);
        Map<LocalDate, Integer> rest = sumByDate(REST_SUM, userID, from, to);

        List<DayTotals> days = new ArrayList<>();
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            days.add(new DayTotals(
                    date,
                    activity.getOrDefault(date, 0),
                    rest.getOrDefault(date, 0)));
        }
        return days;
    }

    /** Each activity is logged on one date, in order. */
    public List<DayEntry> activitiesOn(int userID, LocalDate date) throws SQLException {
        return entriesOn(ACTIVITY_ENTRIES, userID, date);
    }

    /** Each rest entry logged on one date, in order */
    public List<DayEntry> restOn(int userID, LocalDate date) throws SQLException {
        return entriesOn(REST_ENTRIES, userID, date);
    }

    private List<DayEntry> entriesOn(String sql, int userID, LocalDate date) throws SQLException {
        List<DayEntry> entries = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userID);
            statement.setString(2, date.toString());
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    entries.add(new DayEntry(results.getString(1), results.getString(2), results.getInt(3)));
                }
            }
        }
        return entries;
    }

    private Map<LocalDate, Integer> sumByDate(String sql, int userID, LocalDate from, LocalDate to)
            throws SQLException {
        Map<LocalDate, Integer> totals = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userID);
            statement.setString(2, from.toString());
            statement.setString(3, to.toString());
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    totals.put(LocalDate.parse(results.getString(1)), results.getInt("total"));
                }
            }
        }
        return totals;
    }
}
