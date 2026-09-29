package com.example.cab302project.Dashboard;

import com.example.cab302project.Dashboard.Ollama.Response;
import com.example.cab302project.Dashboard.Recommendations.IRecommendationsDAO;
import com.example.cab302project.Dashboard.Recommendations.RecommendationData;
import com.example.cab302project.Dashboard.Recommendations.RecommendationService;
import com.example.cab302project.Database.DashboardDatabaseDAO;
import com.example.cab302project.Database.DatabaseRecommendationsDAO;
import com.example.cab302project.MoodForm.CheckIn;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * This is the controller class of the dashboard feature which will be used to integrate the UI interactions with the feature's logic and database.
 */
public class DashboardController {

    private int userId;
    private DashboardModel dashboard;
    private IDashboardDAO dashboardDAO;

    //Overall statistics section fields
    @FXML
    private Arc streakArc;

    @FXML
    private ProgressBar activityCompleted;

    @FXML
    private Label activitiesCompletedLabel;

    @FXML
    private ProgressBar activitiesMinutesCompleted;

    @FXML
    private Label activitiesMinutesCompletedLabel;

    @FXML
    private Label avgSleep;

    @FXML
    private Label avgStudyStress;

    @FXML
    private Label weeklyStreakLabel;

    @FXML
    private Label recommendationOne;

    @FXML
    private Label recommendationTwo;

    @FXML
    private Label recommendationThree;

    private IRecommendationsDAO recommendationsDAO;
    private RecommendationService recommendationService;

    /**
     * Constructs the dashboard dao for the dashboard.
     */
    public DashboardController() {

        dashboardDAO = new DashboardDatabaseDAO();
        recommendationsDAO = new DatabaseRecommendationsDAO();
        recommendationService = new RecommendationService();
    }

    /**
     * This method initialises the FXML page through proper checks.
     */
    @FXML
//    public void initialize() {
//        setUserId(userId);
//    }

    /**
     * This method loads the dashboard based on the user's data.
     *
     * @param userId: The userID of the logged-in user.
     */
    public void setUserId(int userId) {
        this.userId = userId;
        loadDashboard(userId);
    }

    /**
     * This method aims to set the conditions for loading the dashboard.
     *
     * @param userId: The parameter which takes the logged user's ID to load the correct dashboard.
     */
    private void loadDashboard(int userId) {
        LocalDate today = LocalDate.now();

        LocalDate startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endDate = startDate.plusDays(6);

        //Loading the dashboard model.
        dashboard = new DashboardModel(
                dashboardDAO.getWeeklyCheckInStreak(userId, startDate, endDate),
                dashboardDAO.userActivitiesCompleted(userId),
                dashboardDAO.averageStudyStress(userId),
                dashboardDAO.averageSleep(userId),
                dashboardDAO.totalActivityMinutes(userId),
                dashboardDAO.userActivityGoal(userId)
        );

        FXMLUpdateOverallStats();
        loadRecommendations(userId);
    }

    /**
     * This method updates the statistics in UI based on the user's data stored in the DAO.
     */
    private void FXMLUpdateOverallStats() {
        //To update the activities completed.
        String retrievedActivitiesCompleted = String.valueOf(dashboard.getActivitiesCompleted());
        activitiesCompletedLabel.setText(retrievedActivitiesCompleted);
        activityCompleted.setProgress(Math.min(Double.parseDouble(retrievedActivitiesCompleted), 1.0));

        //To update the activities minutes completed.
        double retrievedActivityGoal = Integer.valueOf(dashboard.getActivityGoal());
        double retrievedActivityMinutes = Integer.valueOf(dashboard.getActivityMinutes());
//        double progress;
//        if(retrievedActivityGoal>0) {
//            progress = retrievedActivityMinutes / retrievedActivityGoal;
//        } else {
//            progress = retrievedActivityMinutes;
//        }
        activitiesMinutesCompletedLabel.setText(String.valueOf(retrievedActivityMinutes) + "/" + String.valueOf(retrievedActivityGoal) + " min");
        activitiesMinutesCompleted.setProgress(Math.min(dashboard.getTotalActivityMinutes(), 1.0));

        //To update the average sleep.
        String retrievedAvgSleep = String.valueOf(dashboard.getAvgSleep());
        avgSleep.setText(retrievedAvgSleep);

        //To update the average study stress
        String retrievedAvgStudyStress = String.valueOf(dashboard.getAvgStudyStress());
        avgStudyStress.setText(retrievedAvgStudyStress);

        //To update the weekly checkins completed.
        int checkinsCompleted = dashboard.getWeeklyCheckinsStreak();
        streakArc.setLength(-360 * (checkinsCompleted / 7.0));
        weeklyStreakLabel.setText(checkinsCompleted + "/7");
    }

    private void loadRecommendations(int userId) {
        recommendationOne.setText("Generating recommendation..");
        recommendationTwo.setText("");
        recommendationThree.setText("");

        List<Map<String, Object>> activityData = recommendationsDAO.getRecentActivityData(userId);
        List<CheckIn> checkinData = recommendationsDAO.getRecentCheckinData(userId);

        RecommendationData data = new RecommendationData(
                activityData,
                checkinData
        );

        recommendationService.generateRecommendations(data,
                response -> Platform.runLater(() -> {
                    if (response == null || response.getResponse() == null) {
                        recommendationOne.setText("Unable to generate recommendations.");
                        recommendationTwo.setText("Unable to generate recommendations.");
                        recommendationThree.setText("Unable to generate recommendations.");
                        return;
                    }

                    String[] recommendations = response.getResponse().split("\\r?\\n");
                    int recommendationIndex = 0;

                    for (String recommendation : recommendations) {
                        recommendation = recommendation.trim();

                        if (recommendation.isEmpty()) {
                            continue;
                        }

                        if (recommendationIndex == 0) {
                            recommendationOne.setText(recommendation);
                        }

                        if (recommendationIndex == 1) {
                            recommendationTwo.setText(recommendation);
                        }

                        if (recommendationIndex == 2) {
                            recommendationThree.setText(recommendation);
                        }

                        recommendationIndex++;
                    }


                }));
    }
}
