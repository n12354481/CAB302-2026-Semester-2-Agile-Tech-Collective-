package com.example.cab302project.Rewards;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Uses the only two thresholds the plates actually fix: Sprout at 3, Flower at 15. */
class RewardsSummaryTest {

    private static final int[] THRESHOLDS = {3, 15};

    private static RewardsSummary summarise(DayState... days) {
        return RewardsSummary.of(List.of(days), THRESHOLDS);
    }

    @Test
    void emptyRunIsAllZeroes() {
        RewardsSummary summary = RewardsSummary.of(List.of(), THRESHOLDS);
        assertEquals(new RewardsSummary(0, 0, 0), summary);
    }

    @Test
    void onlyBalancedAndCappedDaysAreBanked() {
        RewardsSummary summary = summarise(
                DayState.BALANCED, DayState.CAPPED, DayState.ROOTED_NO_FLOWER, DayState.REST_ONLY);
        assertEquals(2, summary.balancedDays());
    }

    @Test
    void streakCountsBackFromTodayAndStopsAtAnEmptyDay() {
        RewardsSummary summary = summarise(
                DayState.BALANCED,        // held, but before the break
                DayState.NOTHING_LOGGED,  // breaks it
                DayState.REST_ONLY,
                DayState.UNDER_BOTH,
                DayState.BALANCED);
        assertEquals(3, summary.currentStreak());
    }

    @Test
    void anEmptyDayTodayEndsTheStreakEvenAfterAGoodRun() {
        RewardsSummary summary = summarise(
                DayState.BALANCED, DayState.BALANCED, DayState.NOTHING_LOGGED);
        assertEquals(0, summary.currentStreak());
        assertEquals(2, summary.balancedDays(), "banked days survive a broken streak");
    }

    @Test
    void stageIsZeroUntilTheFirstThresholdIsPassed() {
        assertEquals(0, summarise(DayState.BALANCED, DayState.BALANCED).stageReached());
    }

    @Test
    void thresholdIsInclusive() {
        assertEquals(1, summarise(DayState.BALANCED, DayState.BALANCED, DayState.BALANCED)
                .stageReached());
    }

    @Test
    void laterThresholdsRaiseTheStage() {
        DayState[] fifteen = new DayState[15];
        java.util.Arrays.fill(fifteen, DayState.BALANCED);
        assertEquals(2, summarise(fifteen).stageReached());
    }

    @Test
    void unclaimedStagesIsWhatIsOwed() {
        RewardsSummary summary = new RewardsSummary(15, 15, 2);
        assertEquals(2, summary.unclaimedStages(0));
        assertEquals(1, summary.unclaimedStages(1));
        assertEquals(0, summary.unclaimedStages(2));
        assertEquals(0, summary.unclaimedStages(5), "never negative");
    }

    @Test
    void nullArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> RewardsSummary.of(null, THRESHOLDS));
        assertThrows(IllegalArgumentException.class, () -> RewardsSummary.of(List.of(), null));
    }
}
