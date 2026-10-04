package com.example.cab302project.Rewards;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * One call for the rewards pane: give it a user and a date range, get back the summary.
 *
 * Ties together the three pieces: read the minutes per day, classify
 * each day against the goals, count the result. The controller should not know that
 * chain exists.
 *
 * Goals are passed in for now. Once the {@code garden_goal} lookup is written they will be
 * read per day from there and callers will not have to change.
 */
public final class RewardsService {

    /** Used until the {@code garden_goal} lookup exists.*/
    public static final int DEFAULT_ACTIVITY_GOAL = 60;

    /** Seven hours. */
    public static final int DEFAULT_REST_GOAL = 420;

    private final DayTotalsDAO dayTotals;
    private final int[] thresholds;

    /**
     * @param thresholds balanced days required per stage, ascending order. Only Sprout = 3 and
     *                   Flower = 15 are resolved, so the caller has to supply  them.
     */
    public RewardsService(Connection connection, int[] thresholds) {
        if (thresholds == null) {
            throw new IllegalArgumentException("thresholds are required");
        }
        this.dayTotals = new DayTotalsDAO(connection);
        this.thresholds = thresholds.clone();
    }

    /** The summary for a range, using the default goals. */
    public RewardsSummary summaryFor(int userID, LocalDate from, LocalDate to) throws SQLException {
        return summaryFor(userID, from, to, DEFAULT_ACTIVITY_GOAL, DEFAULT_REST_GOAL);
    }

    /** The summary for a range, against the goals given. */
    public RewardsSummary summaryFor(int userID, LocalDate from, LocalDate to,
                                     int activityGoal, int restGoal) throws SQLException {
        return RewardsSummary.of(statesFor(userID, from, to, activityGoal, restGoal), thresholds);
    }

    /**
     * The day by day states behind the summary, oldest first. The panel needs these to draw a
     * row of plants, the summary alone cannot say what any individual day looked like.
     */
    public List<DayState> statesFor(int userID, LocalDate from, LocalDate to,
                                    int activityGoal, int restGoal) throws SQLException {
        return dayTotals.forRange(userID, from, to).stream()
                .map(day -> day.state(activityGoal, restGoal))
                .toList();
    }

    /**
     * The minutes behind each day, oldest first. The garden needs these to size each stem
     * and root, a state alone cannot say how tall a plant is.
     */
    public List<DayTotals> totalsFor(int userID, LocalDate from, LocalDate to) throws SQLException {
        return dayTotals.forRange(userID, from, to);
    }

    /** What grew above the ground on one day, for the selected day card. */
    public List<DayEntry> activitiesOn(int userID, LocalDate date) throws SQLException {
        return dayTotals.activitiesOn(userID, date);
    }

    /** What grew below the ground on one day, for the selected day card. */
    public List<DayEntry> restOn(int userID, LocalDate date) throws SQLException {
        return dayTotals.restOn(userID, date);
    }

    /** The last {@code days} days ending today - what the panel shows by default. */
    public RewardsSummary summaryForLastDays(int userID, int days, LocalDate today)
            throws SQLException {
        if (days < 1) {
            throw new IllegalArgumentException("days must be at least 1");
        }
        return summaryFor(userID, today.minusDays(days - 1L), today);
    }
}
