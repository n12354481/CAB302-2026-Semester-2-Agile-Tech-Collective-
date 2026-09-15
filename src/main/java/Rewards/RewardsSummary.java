package Rewards;

import java.util.List;

/**
 * What the rewards panel shows is how many balanced days are stored, how long the current
 * streak is, and which growth stage that adds up to.
 *
 * No database yet/clock. Provided it has the right days and the thresholds, it will be able to count.
 * The thresholds are a parameter rather than constants because the six stage names
 * and counts are still unsettled. Only Sprout = 3 and Flower = 15 are fixed.
 *
 * @param balancedDays  total days that banked a stage over the whole run
 * @param currentStreak days at the end of the run that held the streak (counting back)
 * @param stageReached  how many thresholds the banked days have passed, 0 is not yet sprouted
 */
public record RewardsSummary(int balancedDays, int currentStreak, int stageReached) {

    /**
     * Summarises a run of days.
     *
     * @param days       the days in order, oldest first. The last entry is today.
     * @param thresholds balanced days required per stage, ascending - {@code {3, 15}} means
     *                   stage 1 at 3 banked days and stage 2 at 15
     */
    public static RewardsSummary of(List<DayState> days, int[] thresholds) {
        if (days == null || thresholds == null) {
            throw new IllegalArgumentException("days and thresholds are required");
        }

        int balanced = 0;
        for (DayState day : days) {
            if (day.banksStage()) {
                balanced++;
            }
        }

        // The streak is the unbroken run ending today, count backwards from the end.
        int streak = 0;
        for (int i = days.size() - 1; i >= 0; i--) {
            if (!days.get(i).keepsStreak()) {
                break;
            }
            streak++;
        }

        int stage = 0;
        for (int threshold : thresholds) {
            if (balanced >= threshold) {
                stage++;
            }
        }

        return new RewardsSummary(balanced, streak, stage);
    }

    /**
     * Stages reached but not yet claimed. Claiming itself is stored elsewhere, this only
     * says how many are owed.
     *
     * @param stagesAlreadyClaimed the highest stage the user has already claimed
     */
    public int unclaimedStages(int stagesAlreadyClaimed) {
        return Math.max(0, stageReached - stagesAlreadyClaimed);
    }
}
