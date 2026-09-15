package com.example.cab302project.Rewards;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Uses the only two thresholds the plates actually fix: Sprout at 3, Flower at 15. */
class RewardsSummaryTest {

    private static final int[] THRESHOLDS = {3, 15};

    @Test
    void streakCountsBackFromTodayAndStopsAtAnEmptyDay() {
        RewardsSummary summary = RewardsSummary.of(List.of(
                DayState.BALANCED,        // held, but before the break
                DayState.NOTHING_LOGGED,  // breaks it
                DayState.REST_ONLY,
                DayState.UNDER_BOTH,
                DayState.BALANCED), THRESHOLDS);
        assertEquals(3, summary.currentStreak());
    }
}
