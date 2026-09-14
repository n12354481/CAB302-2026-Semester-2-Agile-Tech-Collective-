package RewardsTests;

import com.example.cab302project.Rewards.DayState;
import com.example.cab302project.Rewards.RewardsSummary;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Uses the only two thresholds the plates actually fix: Sprout at 3, Flower at 15. */
class RewardsSummaryTest {

    private static final int[] THRESHOLDS = {3, 15};

    private static RewardsSummary summarise(DayState... days) {
        return RewardsSummary.of(List.of(days), THRESHOLDS);
    }

    private static DayState[] balancedDays(int count) {
        DayState[] days = new DayState[count];
        Arrays.fill(days, DayState.BALANCED);
        return days;
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
    void stageRisesAsEachThresholdIsReached() {
        assertEquals(0, summarise(balancedDays(2)).stageReached());
        assertEquals(1, summarise(balancedDays(3)).stageReached(), "thresholds are inclusive");
        assertEquals(2, summarise(balancedDays(15)).stageReached());
    }

    @Test
    void unclaimedStagesIsWhatIsOwed() {
        RewardsSummary summary = new RewardsSummary(15, 15, 2);
        assertEquals(2, summary.unclaimedStages(0));
        assertEquals(1, summary.unclaimedStages(1));
        assertEquals(0, summary.unclaimedStages(2));
        assertEquals(0, summary.unclaimedStages(5), "never negative");
    }
}
