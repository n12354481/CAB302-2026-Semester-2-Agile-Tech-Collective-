package Rewards;

import java.time.LocalDate;

/**
 * One reward on the claiming page: what it is, what it counts, and how far along it is.
 *
 * Reward sits in one of three bands. It is counting until {@link #complete()}, then it is
 * ready to claim.
 *
 * The counts are hardcoded for this iteration, nothing here reads the database yet.
 */
public class Reward {

    private final String name;
    private final String counts;
    private final int done;
    private final int target;
    private final String unit;
    private final String description;

    /** Null until the reward is claimed. */
    private LocalDate claimedOn;

    /**
     * @param counts      what is being counted, shown under the name, e.g. "Study sessions"
     * @param unit        what the numbers are, e.g. "days" in "7 of 20 days"
     * @param description the sentence the confirm dialog shows
     */
    public Reward(String name, String counts, int done, int target, String unit, String description) {
        this.name = name;
        this.counts = counts;
        this.done = done;
        this.target = target;
        this.unit = unit;
        this.description = description;
    }

    public String name() {
        return name;
    }

    public String counts() {
        return counts;
    }

    public String description() {
        return description;
    }

    /** "7 of 20 days" while counting, "1 of 1 month (complete)" once the target is met. */
    public String progress() {
        String text = done + " of " + target + " " + unit;
        return complete() ? text + " (complete)" : text;
    }

    /** How full the progress bar is, 0 to 1. */
    public double fraction() {
        return Math.min(1.0, (double) done / target);
    }

    public boolean complete() {
        return done >= target;
    }

    public boolean claimed() {
        return claimedOn != null;
    }

    public LocalDate claimedOn() {
        return claimedOn;
    }

    public void claim(LocalDate date) {
        claimedOn = date;
    }

    /** Puts it back to ready to claim. */
    public void unclaim() {
        claimedOn = null;
    }
}
