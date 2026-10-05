package com.example.cab302project.Rewards;

import java.time.LocalDate;

/**
 * One reward on the claiming page: what it is, what it counts, and how far along it is.
 *
 * Reward sits in one of three bands. It is counting until {@link #complete()}, then it is
 * ready to claim.
 *
 * Built by {@link ClaimsDAO} from the user's logged activities.
 */
public class Reward {

    private final String key;
    private final String name;
    private final String counts;
    private final int done;
    private final int target;
    private final String unit;
    private final String description;

    /** Null until the reward is claimed. */
    private LocalDate claimedOn;

    /**
     * @param key         stays the same if the name is reworded, so a saved claim still matches
     * @param counts      what is being counted, shown under the name, e.g. "Social activities"
     * @param unit        what the numbers are, e.g. "activities" in "7 of 20 activities"
     * @param description the sentence the confirm dialog shows
     */
    public Reward(String key, String name, String counts, int done, int target, String unit,
                  String description) {
        this.key = key;
        this.name = name;
        this.counts = counts;
        this.done = done;
        this.target = target;
        this.unit = unit;
        this.description = description;
    }

    public String key() {
        return key;
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
        if (!complete()) {
            throw new IllegalStateException(name + " is not complete yet");
        }
        claimedOn = date;
    }

    /**
     * A claim read back from the database. Skips the target check, so a cosmetic stays
     * claimed even if the activities behind it are deleted later.
     */
    void restoreClaim(LocalDate date) {
        claimedOn = date;
    }
}

