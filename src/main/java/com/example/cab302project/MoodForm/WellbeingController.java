package com.example.cab302project.MoodForm;

import com.example.cab302project.Database.DatabaseCheckInDAO;
import com.example.cab302project.HelloController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import java.util.Map;
import java.time.LocalDate;

import java.util.List;

public class WellbeingController {

    private int userID;

    private final DatabaseCheckInDAO checkInDAO =
            new DatabaseCheckInDAO();

    @FXML
    private Label checkInCountLabel;

    @FXML
    private Label happyCountLabel;

    @FXML
    private Label calmCountLabel;

    @FXML
    private Label tiredCountLabel;

    @FXML
    private Label anxiousCountLabel;

    @FXML
    private Label sadCountLabel;

    @FXML
    private Label sleepyCountLabel;

    @FXML
    private Label sleepInsightLabel;

    @FXML
    private Label waterInsightLabel;

    @FXML
    private Label stressInsightLabel;

    @FXML
    private LineChart<String, Number> moodChart;

    @FXML
    private CategoryAxis dateAxis;

    @FXML
    private NumberAxis moodAxis;

    public void setUserID(int userID) {
        this.userID = userID;

        loadWellbeingData();
        loadWeeklyMoodCounts();
        loadInsights();
    }

    private HelloController helloController;

    public void setHelloController(HelloController helloController) {
        this.helloController = helloController;
    }

    @FXML
    private void handleNewCheckIn() {
        if (helloController != null) {
            helloController.loadCheckInPage();
        }
    }

    private void loadWellbeingData() {

        List<CheckIn> checkIns =
                checkInDAO.getCheckInsForUser(userID);

        checkInCountLabel.setText(
                "Total check-ins: " + checkIns.size()
        );

        setupMoodAxis();
        loadMoodChart(checkIns);

        System.out.println(
                "Loaded " + checkIns.size()
                        + " check-ins for user "
                        + userID
        );
    }

    private void loadMoodChart(List<CheckIn> checkIns) {

        // Remove any old graph data
        moodChart.getData().clear();

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.setName("Mood");

        int checkInNumber = 1;

        for (CheckIn checkIn : checkIns) {

            String label =
                    checkIn.getCheckinDate().toString()
                            + " #" + checkInNumber;

            XYChart.Data<String, Number> point =
                    new XYChart.Data<>(
                            label,
                            checkIn.getEmotionToday()
                    );

            series.getData().add(point);

            checkInNumber++;
        }

        moodChart.getData().add(series);
    }

    private void setupMoodAxis() {

        moodAxis.setTickLabelFormatter(
                new StringConverter<Number>() {

                    @Override
                    public String toString(Number value) {

                        return switch (value.intValue()) {
                            case 1 -> "Struggling";
                            case 2 -> "Not Great";
                            case 3 -> "Okay";
                            case 4 -> "Good";
                            case 5 -> "Great";
                            default -> "";
                        };
                    }

                    @Override
                    public Number fromString(String string) {
                        return switch (string) {
                            case "Struggling" -> 1;
                            case "Not Great" -> 2;
                            case "Okay" -> 3;
                            case "Good" -> 4;
                            case "Great" -> 5;
                            default -> 0;
                        };
                    }
                }
        );
    }

    private void loadWeeklyMoodCounts() {

        Map<String, Integer> moodCounts =
                checkInDAO.getMoodCountsForLastWeek(userID);

        happyCountLabel.setText(
                "Happy: " + moodCounts.get("Happy")
        );

        calmCountLabel.setText(
                "Calm: " + moodCounts.get("Calm")
        );

        tiredCountLabel.setText(
                "Tired: " + moodCounts.get("Tired")
        );

        anxiousCountLabel.setText(
                "Anxious: " + moodCounts.get("Anxious")
        );

        sadCountLabel.setText(
                "Sad: " + moodCounts.get("Sad")
        );

        sleepyCountLabel.setText(
                "Sleepy: " + moodCounts.get("Sleepy")
        );
    }

    private void loadInsights() {

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        List<CheckIn> weeklyCheckIns =
                checkInDAO.getCheckInsBetweenDates(
                        userID,
                        weekStart,
                        today
                );

        if (weeklyCheckIns.isEmpty()) {
            sleepInsightLabel.setText("Not enough data");
            waterInsightLabel.setText("Not enough data");
            stressInsightLabel.setText("Not enough data");
            return;
        }

        loadSleepInsight(weeklyCheckIns);
        loadWaterInsight(weeklyCheckIns);
        loadStressInsight(weeklyCheckIns);
    }

    private void loadSleepInsight(List<CheckIn> checkIns) {

        double total = 0;

        for (CheckIn checkIn : checkIns) {
            total += checkIn.getSleep();
        }

        double average = total / checkIns.size();

        String message;

        if (average < 1) {
            message = "Your sleep has been quite low this week.";
        } else if (average < 2) {
            message = "You may benefit from getting more sleep.";
        } else if (average < 2.5) {
            message = "Your sleep has been good this week.";
        } else {
            message = "Your sleep has been excellent this week.";
        }

        sleepInsightLabel.setText(message);
    }

    private void loadWaterInsight(List<CheckIn> checkIns) {

        double total = 0;

        for (CheckIn checkIn : checkIns) {
            total += checkIn.getWater();
        }

        double average = total / checkIns.size();

        String message;

        if (average < 2) {
            message = "Your water intake has been low this week.";
        } else if (average < 4) {
            message = "Your water intake has been moderate this week.";
        } else {
            message = "You've been keeping up your water intake well.";
        }

        waterInsightLabel.setText(message);
    }

    private void loadStressInsight(List<CheckIn> checkIns) {

        double total = 0;

        for (CheckIn checkIn : checkIns) {
            total += checkIn.getStudyStress();
        }

        double average = total / checkIns.size();

        String message;

        if (average <= 3) {
            message = "Your study stress has been low this week.";
        } else if (average <= 6) {
            message = "Your study stress has been moderate this week.";
        } else {
            message = "Your study stress has been high this week.";
        }

        stressInsightLabel.setText(message);
    }
}