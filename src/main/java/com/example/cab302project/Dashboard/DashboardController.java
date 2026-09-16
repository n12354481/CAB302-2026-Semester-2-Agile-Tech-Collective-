package com.example.cab302project.Dashboard;

import com.example.cab302project.Database.DashboardDatabaseDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;

import java.time.LocalDate;

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


    /**
     * Constructs the dashboard dao for the dashboard.
     */
    public DashboardController() {
        dashboardDAO = new DashboardDatabaseDAO();
    }

    /**
     * This method initialises the FXML page through proper checks.
     */
    @FXML
    public void initialize() {
        setUserId(userId);
    }

    /**
     * This method loads the dashboard based on the user's data.
     * @param userId: The userID of the logged-in user.
     */
    public void setUserId(int userId) {
        this.userId = userId;
        loadDashboard(userId);
    }

    /**
     * This method aims to set the conditions for loading the dashboard.
     * @param userId: The parameter which takes the logged user's ID to load the correct dashboard.
     */
    private void loadDashboard(int userId) {
        LocalDate today = LocalDate.now();

        LocalDate startDate = today.minusDays(today.getDayOfWeek().getValue() -1);
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

        FXMLUpdateOverallStats();;
    }

    /**
     * This method updates the statistics in UI based on the user's data stored in the DAO.
     */
    private void FXMLUpdateOverallStats()
    {
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
        streakArc.setLength(-360 * (checkinsCompleted/7.0));
        weeklyStreakLabel.setText(checkinsCompleted + "/7");
    }
}
