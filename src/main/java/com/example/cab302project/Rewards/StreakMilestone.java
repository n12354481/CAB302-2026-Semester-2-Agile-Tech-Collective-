package com.example.cab302project.Rewards;

import java.util.OptionalInt;

/** The next streak length worth aiming for, shown under the streak count. */
public final class StreakMilestone {

    private StreakMilestone() {
    }

    private static final int[] MILESTONES = {3, 7, 14, 30, 60};

    /**
     * The first milestone above {@code streak}. Empty with no streak to build on, and empty
     * once the streak is past the last milestone.
     */
    public static OptionalInt next(int streak) {
        if (streak <= 0) {
            return OptionalInt.empty();
        }
        for (int milestone : MILESTONES) {
            if (milestone > streak) {
                return OptionalInt.of(milestone);
            }
        }
        return OptionalInt.empty();
    }
}
