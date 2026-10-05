package RewardsTests;

import com.example.cab302project.Rewards.Reward;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
/** Which band a reward lands in comes from these, so each one is checked. */
class RewardTest {

    private static Reward counting() {
        return new Reward("jacaranda", "Jacaranda", "Study sessions", 7, 10, "sessions", "why it was earned");
    }

    private static Reward finished() {
        return new Reward("garden-gnome", "Garden gnome", "Months active", 1, 1, "month", "why it was earned");
    }

    @Test
    void cannotClaimBeforeTheTargetIsMet() {
        Reward reward = counting();
        assertThrows(IllegalStateException.class, () -> reward.claim(LocalDate.of(2026, 9, 12)));
        assertFalse(reward.claimed());
    }
    @Test
    void meetingTheTargetMakesItReadyToClaim() {
        assertFalse(counting().complete());
        assertTrue(finished().complete());
        assertFalse(finished().claimed(), "ready is not the same as claimed");
    }

    @Test
    void progressReadsAsDoneOfTarget() {
        assertEquals("7 of 10 sessions", counting().progress());
        assertEquals("1 of 1 month (complete)", finished().progress());
    }

    @Test
    void fractionIsHowFullTheBarShouldBeAndNeverPastFull() {
        assertEquals(0.7, counting().fraction(), 0.0001);
        Reward over = new Reward("eucalyptus", "Eucalyptus", "Physical movement", 20, 15, "sessions", "why");
        assertEquals(1.0, over.fraction());
    }

    @Test
    void claimingRemembersTheDay() {
        Reward reward = finished();
        assertNull(reward.claimedOn());

        LocalDate day = LocalDate.of(2026, 9, 12);
        reward.claim(day);
        assertTrue(reward.claimed());
        assertEquals(day, reward.claimedOn());
    }
}
