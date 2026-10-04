package RewardsTests;

import com.example.cab302project.Rewards.StreakMilestone;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreakMilestoneTest {

    @Test
    void noStreakHasNoMilestone() {
        assertEquals(OptionalInt.empty(), StreakMilestone.next(0));
    }

    @Test
    void nextMilestoneIsTheFirstOneAboveTheStreak() {
        assertEquals(OptionalInt.of(3), StreakMilestone.next(2));
        assertEquals(OptionalInt.of(14), StreakMilestone.next(7));
        assertEquals(OptionalInt.of(60), StreakMilestone.next(59));
    }

    @Test
    void pastTheLastMilestoneThereIsNoneLeft() {
        assertEquals(OptionalInt.empty(), StreakMilestone.next(61));
    }
}
