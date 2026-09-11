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

public class DashboardController {
    private int userId;
    private DashboardModel dashboard;
    private IDashboardDAO dashboardDAO;

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

    public DashboardController() {
        dashboardDAO = new DashboardDatabaseDAO();
    }

    @FXML
    public void initialize() {
        setUserId(1);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadDashboard(userId);
    }

    private void loadDashboard(int userId) {
        LocalDate today = LocalDate.now();

        LocalDate startDate = today.minusDays(today.getDayOfWeek().getValue() -1);
        LocalDate endDate = startDate.plusDays(6);

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

    private void FXMLUpdateOverallStats()
    {
        String retrievedActivitiesCompleted = String.valueOf(dashboard.getActivitiesCompleted());
        activitiesCompletedLabel.setText(retrievedActivitiesCompleted);
        activityCompleted.setProgress(Math.min(Double.parseDouble(retrievedActivitiesCompleted), 1.0));


        double retrievedActivityGoal = Integer.valueOf(dashboard.getActivityGoal());
        double retrievedActivityMinutes = Integer.valueOf(dashboard.getActivityMinutes());
        double progress;
        if(retrievedActivityGoal>0) {
            progress = retrievedActivityMinutes / retrievedActivityGoal;
        } else {
            progress = retrievedActivityMinutes;
        }

        activitiesMinutesCompletedLabel.setText(String.valueOf(retrievedActivityMinutes) + "/" + String.valueOf(retrievedActivityGoal) + " min");
        activitiesMinutesCompleted.setProgress(Math.min(progress, 1.0));

        String retrievedAvgSleep = String.valueOf(dashboard.getAvgSleep());
        avgSleep.setText(retrievedAvgSleep);

        String retrievedAvgStudyStress = String.valueOf(dashboard.getAvgStudyStress());
        avgStudyStress.setText(retrievedAvgStudyStress);

        int checkinsCompleted = dashboard.getWeeklyCheckinsStreak();
        streakArc.setLength(-360 * (checkinsCompleted/7.0));
        weeklyStreakLabel.setText(checkinsCompleted + "/7");
    }






}
