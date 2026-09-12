package com.example.cab302project.CheckIn;

import com.example.cab302project.Database.DatabaseCheckInDAO;
import com.example.cab302project.MoodForm.CheckIn;
import com.example.cab302project.MoodForm.ICheckInDAO;

import java.time.LocalDate;
import java.util.List;

public class CheckInDAOTest {

    public static void main(String[] args) {

        ICheckInDAO checkInDAO =
                new DatabaseCheckInDAO();

        /*
         * Change this if your test user's userID
         * is not 1.
         */
        int userID = 1;

        LocalDate today =
                LocalDate.now();

        CheckIn firstCheckIn =
                new CheckIn(
                        userID,
                        today,
                        3,
                        6,
                        1500,
                        4,
                        List.of(
                                "Tired",
                                "Anxious"
                        )
                );

        CheckIn secondCheckIn =
                new CheckIn(
                        userID,
                        today,
                        5,
                        8,
                        2000,
                        2,
                        List.of(
                                "Happy",
                                "Calm"
                        )
                );

        boolean firstSaved =
                checkInDAO.saveCheckIn(
                        firstCheckIn
                );

        boolean secondSaved =
                checkInDAO.saveCheckIn(
                        secondCheckIn
                );

        System.out.println(
                "First check-in saved: "
                        + firstSaved
        );

        System.out.println(
                "Second check-in saved: "
                        + secondSaved
        );

        System.out.println();
        System.out.println(
                "=== TODAY'S CHECK-INS ==="
        );

        List<CheckIn> todaysCheckIns =
                checkInDAO.getCheckInsForDate(
                        userID,
                        today
                );

        for (CheckIn checkIn : todaysCheckIns) {

            System.out.println(
                    "Check-in ID: "
                            + checkIn.getCheckinID()
            );

            System.out.println(
                    "Date: "
                            + checkIn.getCheckinDate()
            );

            System.out.println(
                    "Emotion: "
                            + checkIn.getEmotionToday()
            );

            System.out.println(
                    "Sleep: "
                            + checkIn.getSleep()
            );

            System.out.println(
                    "Water: "
                            + checkIn.getWater()
            );

            System.out.println(
                    "Stress: "
                            + checkIn.getStudyStress()
            );

            System.out.println(
                    "Moods: "
                            + checkIn.getMoods()
            );

            System.out.println(
                    "-------------------------"
            );
        }
    }
}