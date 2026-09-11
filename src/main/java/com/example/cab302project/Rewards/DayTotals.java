package com.example.cab302project.Rewards;

import java.time.LocalDate;

/**
 * One day: activity above the ground line and rest below it. A day with nothing
 * logged counts as  a row, with both at zero. The garden has bare soil for it, and the
 * streak needs to see it for it to be broken.
 */
public record DayTotals(LocalDate date, int activityMinutes, int restMinutes) {

    public DayTotals {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }
        if (activityMinutes < 0 || restMinutes < 0) {
            throw new IllegalArgumentException("minutes cannot be negative");
        }
    }

    /** The state of this day under the given goals. */
    public DayState state(int activityGoal, int restGoal) {
        return DayState.classify(activityMinutes, restMinutes, activityGoal, restGoal);
    }
}
