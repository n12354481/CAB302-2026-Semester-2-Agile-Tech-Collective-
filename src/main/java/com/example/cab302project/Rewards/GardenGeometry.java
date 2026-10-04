package com.example.cab302project.Rewards;

import java.util.List;

/**
 * The garden's maths with no JavaFX in it, so it can be unit tested.
 *
 * <p>Everything is in the garden's own fixed 740 x 340 space. {@link GardenPane} draws at
 * these numbers and scales the whole drawing to fit its card.
 *
 * <p>Stems and roots are scaled against each day's own goal, so the goal lines stay put
 * even if a user's goals change from one day to the next.
 */
public final class GardenGeometry {

    private GardenGeometry() {
    }

    public static final double WIDTH = 740;
    public static final double HEIGHT = 340;

    /** The soil line. Stems grow up from it, roots down. */
    public static final double GROUND_Y = 180;

    /** The left 70 px holds the goal labels. */
    public static final double LEFT = 70;
    public static final double RIGHT = 730;

    /** How far the stem reaches when the activity goal is met. */
    public static final double ACTIVITY_GOAL_HEIGHT = 70;

    /** How deep the roots reach when the rest goal is met. */
    public static final double REST_GOAL_DEPTH = 77;

    private static final double MIN_STEM = 12;
    private static final double MIN_ROOT = 10;
    private static final double MAX_ACTIVITY_RATIO = 2.0;
    private static final double MAX_REST_RATIO = 10.0 / 7;

    /** What sits on top of a day's plant. */
    public enum PlantTop {
        /** Both goals met. */
        FLOWER,
        /** Some activity, not balanced. */
        BUD,
        /** Rest only: a short stub so the roots do not float. */
        STUB,
        /** Nothing logged: a dashed seed at the soil line. */
        SEED
    }

    /** Width of one day's column. */
    public static double columnWidth(int days) {
        return (RIGHT - LEFT) / days;
    }

    /** Centre x of the plant in column {@code i}. */
    public static double plantX(int i, int days) {
        return LEFT + i * columnWidth(days) + columnWidth(days) / 2;
    }

    /** Stem height above the soil. 0 means no stem; otherwise 12 to 2x the goal height. */
    public static double stemHeight(int activityMinutes, int activityGoal) {
        if (activityMinutes <= 0) {
            return 0;
        }
        double ratio = Math.min((double) activityMinutes / activityGoal, MAX_ACTIVITY_RATIO);
        return Math.max(MIN_STEM, ratio * ACTIVITY_GOAL_HEIGHT);
    }

    /** Root depth below the soil. 0 means no roots; otherwise 10 to 10/7 of the goal depth. */
    public static double rootDepth(int restMinutes, int restGoal) {
        if (restMinutes <= 0) {
            return 0;
        }
        double ratio = Math.min((double) restMinutes / restGoal, MAX_REST_RATIO);
        return Math.max(MIN_ROOT, ratio * REST_GOAL_DEPTH);
    }

    /** Capped days are still balanced, so they flower too. */
    public static PlantTop topFor(DayState state) {
        return switch (state) {
            case BALANCED, CAPPED -> PlantTop.FLOWER;
            case REST_ONLY -> PlantTop.STUB;
            case NOTHING_LOGGED -> PlantTop.SEED;
            case UNDER_BOTH, FLOWERED_SHALLOW, ROOTED_NO_FLOWER -> PlantTop.BUD;
        };
    }

    /** True when every day in the range is bare soil, which shows the empty garden. */
    public static boolean nothingPlanted(List<DayState> days) {
        return days.stream().allMatch(day -> day == DayState.NOTHING_LOGGED);
    }
}
