package com.example.cab302project.Rewards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Day states against the default goals: 60 min activity, 420 min rest. */
class DayStateTest {

    private static final int ACTIVITY_GOAL = 60;
    private static final int REST_GOAL = 420;

    private static DayState classify(int activity, int rest) {
        return DayState.classify(activity, rest, ACTIVITY_GOAL, REST_GOAL);
    }

    @Test
    void eachMixOfGoalsGetsItsOwnState() {
        assertEquals(DayState.NOTHING_LOGGED, classify(0, 0));
        assertEquals(DayState.REST_ONLY, classify(0, 480));
        assertEquals(DayState.UNDER_BOTH, classify(20, 300));
        assertEquals(DayState.FLOWERED_SHALLOW, classify(90, 300));
        assertEquals(DayState.ROOTED_NO_FLOWER, classify(20, 480));
        assertEquals(DayState.BALANCED, classify(60, 420));
        assertEquals(DayState.CAPPED, classify(240, 480));
    }

    @Test
    void cappedActivityCannotRescueAMissedActivityGoal() {
        // 300 min counts as 180, which is still the same side of a 200 min goal.
        assertEquals(DayState.UNDER_BOTH, DayState.classify(300, 300, 200, REST_GOAL));
    }

    @Test
    void everyStateButNothingLoggedHoldsTheStreak() {
        for (DayState state : DayState.values()) {
            assertEquals(state != DayState.NOTHING_LOGGED, state.keepsStreak(), state.name());
        }
    }

    @Test
    void negativeMinutesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> classify(-1, 0));
    }
}
