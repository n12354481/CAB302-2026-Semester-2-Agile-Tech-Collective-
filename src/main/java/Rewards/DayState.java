package Rewards;

/**
 * What one day's plant looks like, given from the minutes logged that day against the
 * goal in effect.
 *
 * <p>Activity is capped at {@link #ACTIVITY_CAP_MINUTES} so one huge day cannot hugely impact a week.
 * Only a balanced day is stored in growth stage, every state except NOTHING_LOGGED holds the
 * streak.
 */
public enum DayState {

    /** No entries at all, bare soil */
    NOTHING_LOGGED(false, false),

    /** Rest logged, no activity and therefore stem */
    REST_ONLY(true, false),

    /** Logged something, but short of both goals */
    UNDER_BOTH(true, false),

    /** Hit the activity goal, missed the rest goal means a flower on a shallow root */
    FLOWERED_SHALLOW(true, false),

    /** Rested well, activity under goal, deep root but no flower */
    ROOTED_NO_FLOWER(true, false),

    /** Both goals met */
    BALANCED(true, true),

    /** Balanced, but over the activity cap */
    CAPPED(true, true);

    /** Above this, extra activity minutes stop counting */
    public static final int ACTIVITY_CAP_MINUTES = 180;

    private final boolean keepsStreak;
    private final boolean banksStage;

    DayState(boolean keepsStreak, boolean banksStage) {
        this.keepsStreak = keepsStreak;
        this.banksStage = banksStage;
    }

    public boolean keepsStreak() {
        return keepsStreak;
    }

    public boolean banksStage() {
        return banksStage;
    }

    /**
     * Works out the state of a single day.
     *
     * @param activityMinutes minutes of activity logged that day (uncapped)
     * @param restMinutes     minutes of rest logged that day, sleep and purposeful rest together
     * @param activityGoal    the activity goal in effect on that day
     * @param restGoal        the rest goal in effect on that day
     *
     */
    public static DayState classify(int activityMinutes, int restMinutes,
                                    int activityGoal, int restGoal) {
        if (activityMinutes < 0 || restMinutes < 0) {
            throw new IllegalArgumentException("minutes cannot be negative");
        }

        boolean overCap = activityMinutes > ACTIVITY_CAP_MINUTES;
        int countedActivity = Math.min(activityMinutes, ACTIVITY_CAP_MINUTES);

        boolean hitActivity = countedActivity >= activityGoal;
        boolean hitRest = restMinutes >= restGoal;

        if (activityMinutes == 0 && restMinutes == 0) {
            return NOTHING_LOGGED;
        }
        // No stem at all
        if (activityMinutes == 0) {
            return REST_ONLY;
        }
        if (hitActivity && hitRest) {
            return overCap ? CAPPED : BALANCED;
        }
        if (hitActivity) {
            return FLOWERED_SHALLOW;
        }
        if (hitRest) {
            return ROOTED_NO_FLOWER;
        }
        return UNDER_BOTH;
    }
}
