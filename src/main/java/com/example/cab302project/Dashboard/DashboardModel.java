package com.example.cab302project.Dashboard;

/**
 * This class aims to set the dashboard model's fields.
 */
public class DashboardModel {
    private int weeklyCheckinsStreak;
    private int activitiesCompleted;
    private double avgStudyStress;
    private double avgSleep;
    private int activityMinutes;
    private int activityGoal;

    /**
     * Constructs the dashboard model's fields.
     * @param weeklyCheckinsStreak: The overall statistic's weekly checkin field.
     * @param activitiesCompleted: The overall statistic's activities completed field.
     * @param avgStudyStress: The overall statistic's average study stress field.
     * @param avgSleep: The overall statistic's average sleep field.
     * @param activityMinutes: The overall statistic's activity minutes field.
     * @param activityGoal: The overall statistics activities goal minutes field.
     */
    public DashboardModel(int weeklyCheckinsStreak, int activitiesCompleted, double avgStudyStress, double avgSleep, int activityMinutes, int activityGoal)
    {
        this.weeklyCheckinsStreak = weeklyCheckinsStreak;
        this.activitiesCompleted = activitiesCompleted;
        this.avgSleep = avgSleep;
        this.avgStudyStress = avgStudyStress;
        this.activityMinutes = activityMinutes;
        this.activityGoal = activityGoal;
    }

    /**
     * Getter for weekly check-ins
     * @return: Returns the weekly check-ins field.
     */
    public int getWeeklyCheckinsStreak()
    {
        return weeklyCheckinsStreak;
    }

    /**
     * Getter for activities completed.
     * @return: Returns the activities completed field.
     */
    public int getActivitiesCompleted()
    {
        return activitiesCompleted;
    }

    /**
     * Getter for average study stress
     * @return: Returns the average study stress field.
     */
    public double getAvgStudyStress()
    {
        return avgStudyStress;
    }

    /**
     * Getter for  average sleep
     * @return: Returns the average sleep field.
     */
    public double getAvgSleep()
    {
        return avgSleep;
    }

    /**
     * Getter for  activity minutes
     * @return: Returns the activity minutes field.
     */
    public int getActivityMinutes()
    {
        return activityMinutes;
    }

    /**
     * Getter for  activity goal
     * @return: Returns the activity goals field.
     */
    public int getActivityGoal()
    {
        return activityGoal;
    }
}
