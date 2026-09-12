package com.example.cab302project.Rewards;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Which band a reward lands in comes from these, so each one is checked. */
class RewardTest {

    private static Reward counting() {
        return new Reward("Jacaranda", "Study sessions", 7, 10, "sessions", "why it was earned");
    }

    private static Reward finished() {
        return new Reward("Garden gnome", "Months active", 1, 1, "month", "why it was earned");
    }

    @Test
    void underTheTargetIsStillCounting() {
        assertFalse(counting().complete());
    }

    @Test
    void meetingTheTargetIsReadyToClaim() {
        Reward reward = finished();
        assertTrue(reward.complete());
        assertFalse(reward.claimed(), "ready is not the same as claimed");
    }

    @Test
    void progressReadsAsDoneOfTarget() {
        assertEquals("7 of 10 sessions", counting().progress());
    }

    @Test
    void aFinishedRewardSaysSoInItsProgress() {
        assertEquals("1 of 1 month (complete)", finished().progress());
    }

    @Test
    void fractionIsHowFullTheBarShouldBe() {
        assertEquals(0.7, counting().fraction(), 0.0001);
    }

    @Test
    void fractionNeverGoesPastFull() {
        Reward over = new Reward("Eucalyptus", "Physical movement", 20, 15, "sessions", "why");
        assertEquals(1.0, over.fraction());
    }

    @Test
    void claimingRemembersTheDay() {
        Reward reward = finished();
        LocalDate day = LocalDate.of(2026, 9, 12);
        reward.claim(day);

        assertTrue(reward.claimed());
        assertEquals(day, reward.claimedOn());
    }

    @Test
    void aRewardIsNotClaimedToStartWith() {
        assertFalse(finished().claimed());
        assertEquals(null, finished().claimedOn());
    }
}
