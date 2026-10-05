package RewardsTests;

import com.example.cab302project.Rewards.DayState;
import com.example.cab302project.Rewards.GardenGeometry;
import com.example.cab302project.Rewards.GardenGeometry.PlantTop;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The garden's drawing maths, against the default goals: 60 min activity, 420 min rest. */
class GardenGeometryTest {

    private static final int ACTIVITY_GOAL = 60;
    private static final int REST_GOAL = 420;
    private static final double DELTA = 1e-9;

    @Test
    void noActivityDrawsNoStem() {
        assertEquals(0, GardenGeometry.stemHeight(0, ACTIVITY_GOAL), DELTA);
    }

    @Test
    void oneMinuteOfActivityGetsTheMinimumStem() {
        assertEquals(12, GardenGeometry.stemHeight(1, ACTIVITY_GOAL), DELTA);
    }

    @Test
    void meetingTheActivityGoalReachesTheGoalLine() {
        assertEquals(70, GardenGeometry.stemHeight(ACTIVITY_GOAL, ACTIVITY_GOAL), DELTA);
    }

    @Test
    void stemStopsGrowingAtTwiceTheGoal() {
        assertEquals(140, GardenGeometry.stemHeight(3 * ACTIVITY_GOAL, ACTIVITY_GOAL), DELTA);
    }

    @Test
    void noRestDrawsNoRoots() {
        assertEquals(0, GardenGeometry.rootDepth(0, REST_GOAL), DELTA);
    }

    @Test
    void meetingTheRestGoalReachesTheGoalLine() {
        assertEquals(77, GardenGeometry.rootDepth(REST_GOAL, REST_GOAL), DELTA);
    }

    @Test
    void rootsStopGrowingAtTenSeventhsOfTheGoal() {
        // 15 hr against a 7 hr goal caps at 10 hr worth of depth.
        assertEquals(110, GardenGeometry.rootDepth(15 * 60, REST_GOAL), DELTA);
    }

    @Test
    void onlyBalancedDaysFlower() {
        assertEquals(PlantTop.FLOWER, GardenGeometry.topFor(DayState.BALANCED));
        assertEquals(PlantTop.FLOWER, GardenGeometry.topFor(DayState.CAPPED));
        assertEquals(PlantTop.BUD, GardenGeometry.topFor(DayState.UNDER_BOTH));
        assertEquals(PlantTop.BUD, GardenGeometry.topFor(DayState.FLOWERED_SHALLOW));
        assertEquals(PlantTop.BUD, GardenGeometry.topFor(DayState.ROOTED_NO_FLOWER));
        assertEquals(PlantTop.STUB, GardenGeometry.topFor(DayState.REST_ONLY));
        assertEquals(PlantTop.SEED, GardenGeometry.topFor(DayState.NOTHING_LOGGED));
    }

    @Test
    void emptyWhenAllFourteenDaysAreNothingLogged() {
        assertTrue(GardenGeometry.nothingPlanted(Collections.nCopies(14, DayState.NOTHING_LOGGED)));
    }

    @Test
    void notEmptyWhenAnyDayHasSomething() {
        List<DayState> days = new ArrayList<>(Collections.nCopies(14, DayState.NOTHING_LOGGED));
        days.set(13, DayState.REST_ONLY);
        assertFalse(GardenGeometry.nothingPlanted(days));
    }
}
