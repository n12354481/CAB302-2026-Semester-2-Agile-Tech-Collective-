
package CheckInTests;

import com.example.cab302project.MoodForm.CheckIn;
import com.example.cab302project.MoodForm.ICheckInDAO;
import com.example.cab302project.Database.DatabaseCheckInDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CheckInTestClass {

    private CheckIn checkIn;
    private ICheckInDAO checkInDAO;
    private LocalDate testDate;

    @BeforeEach
    public void setUp() {

        testDate = LocalDate.of(2026, 10, 20);

        checkIn = new CheckIn(
                1,
                testDate,
                4,
                2,
                5,
                6,
                List.of("Happy", "Calm")
        );

        checkInDAO = new DatabaseCheckInDAO();
    }

    // Check-in model tests

    @Test
    public void testCheckInUserID() {
        assertEquals(1, checkIn.getUserID());
    }

    @Test
    public void testCheckInDate() {
        assertEquals(testDate, checkIn.getCheckinDate());
    }

    @Test
    public void testEmotionRating() {
        assertEquals(4, checkIn.getEmotionToday());
    }

    @Test
    public void testSleepValue() {
        assertEquals(2, checkIn.getSleep());
    }

    @Test
    public void testWaterIntake() {
        assertEquals(5, checkIn.getWater());
    }

    @Test
    public void testStudyStress() {
        assertEquals(6, checkIn.getStudyStress());
    }

    @Test
    public void testMultipleMoods() {
        assertEquals(
                List.of("Happy", "Calm"),
                checkIn.getMoods()
        );
    }

    @Test
    public void testCheckInIDUpdate() {
        checkIn.setCheckinID(10);

        assertEquals(10, checkIn.getCheckinID());
    }

    @Test
    public void testSavedCheckInConstructor() {

        CheckIn savedCheckIn = new CheckIn(
                15,
                1,
                testDate,
                5,
                3,
                6,
                2,
                List.of("Happy")
        );

        assertEquals(15, savedCheckIn.getCheckinID());
        assertEquals(5, savedCheckIn.getEmotionToday());
    }

    // Database retrieval test

    @Test
    public void testNoCheckInsForUnknownUser() {

        int userID = -999;

        List<CheckIn> results =
                checkInDAO.getCheckInsForUser(userID);

        assertEquals(new ArrayList<>(), results);
    }
}
