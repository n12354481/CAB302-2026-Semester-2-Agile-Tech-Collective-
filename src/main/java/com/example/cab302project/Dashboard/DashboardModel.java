package com.example.cab302project.Dashboard;

public class DashboardModel {
    private int weeklyCheckinsStreak;
    private int activitiesCompleted;
    private double avgStudyStress;
    private double avgSleep;
    private int activityMinutes;
    private int activityGoal;

    public DashboardModel(int weeklyCheckinsStreak, int activitiesCompleted, double avgStudyStress, double avgSleep, int activityMinutes, int activityGoal)
    {
        this.weeklyCheckinsStreak = weeklyCheckinsStreak;
        this.activitiesCompleted = activitiesCompleted;
        this.avgSleep = avgSleep;
        this.avgStudyStress = avgStudyStress;
        this.activityMinutes = activityMinutes;
        this.activityGoal = activityGoal;
    }

    public int getWeeklyCheckinsStreak()
    {
        return weeklyCheckinsStreak;
    }

    public int getActivitiesCompleted()
    {
        return activitiesCompleted;
    }

    public double getAvgStudyStress()
    {
        return avgStudyStress;
    }

    public double getAvgSleep()
    {
        return avgSleep;
    }

    public int getActivityMinutes()
    {
        return activityMinutes;
    }

    public int getActivityGoal()
    {
        return activityGoal;
    }
}
