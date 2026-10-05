package com.example.cab302project.Dashboard;

import com.example.cab302project.Dashboard.CommunityInsights.CommunityInsightsData;
import com.example.cab302project.Dashboard.CommunityInsights.CommunityService;
import com.example.cab302project.Dashboard.CommunityInsights.ICommunityInsightsDAO;
import com.example.cab302project.Dashboard.Recommendations.IRecommendationsDAO;
import com.example.cab302project.Dashboard.Recommendations.RecommendationData;
import com.example.cab302project.Dashboard.Recommendations.RecommendationService;
import com.example.cab302project.Database.DatabaseCommunityInsightsDAO;
import com.example.cab302project.Database.DatabaseRecommendationsDAO;

import java.util.List;

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

    private RecommendationData recommendations;
    private CommunityInsightsData insights;

    /**
     * Constructs the dashboard model's fields.
     * @param weeklyCheckinsStreak: The overall statistic's weekly checkin field.
     * @param activitiesCompleted: The overall statistic's activities completed field.
     * @param avgStudyStress: The overall statistic's average study stress field.
     * @param avgSleep: The overall statistic's average sleep field.
     * @param activityMinutes: The overall statistic's activity minutes field.
     * @param activityGoal: The overall statistics activities goal minutes field.
     * @param insights: The insights class which retrieves all the insights data.
     * @param recommendations: The recommendations class which retrieves all the recommendations' data.
     */
    public DashboardModel(int weeklyCheckinsStreak, int activitiesCompleted, double avgStudyStress, double avgSleep, int activityMinutes, int activityGoal, RecommendationData recommendations, CommunityInsightsData insights)
    {
        this.weeklyCheckinsStreak = weeklyCheckinsStreak;
        this.activitiesCompleted = activitiesCompleted;
        this.avgSleep = avgSleep;
        this.avgStudyStress = avgStudyStress;
        this.activityMinutes = activityMinutes;
        this.activityGoal = activityGoal;
        this.insights = insights;
        this.recommendations = recommendations;
    }

    /**
     * Getter for weekly check-ins
     * @return Returns the weekly check-ins field.
     */
    public int getWeeklyCheckinsStreak()
    {
        return weeklyCheckinsStreak;
    }

    /**
     * Getter for activities completed.
     * @return Returns the activities completed field.
     */
    public int getActivitiesCompleted()
    {
        return activitiesCompleted;
    }

    /**
     * Getter for average study stress
     * @return Returns the average study stress field.
     */
    public double getAvgStudyStress()
    {
        return avgStudyStress;
    }

    /**
     * Getter for  average sleep
     * @return Returns the average sleep field.
     */
    public double getAvgSleep()
    {
        return avgSleep;
    }

    /**
     * Getter for  activity minutes
     * @return Returns the activity minutes field.
     */
    public int getActivityMinutes()
    {
        return activityMinutes;
    }

    /**
     * Getter for  activity goal
     * @return Returns the activity goals field.
     */
    public int getActivityGoal()
    {
        return activityGoal;
    }

    /**
     * Method for calculating progress
     * @return Returns the spent activity minutes
     */
    public double getTotalActivityMinutes()
    {
        double progress;

        if(activityGoal<=0)
        {
            progress = activityMinutes;
        }
        else{
            progress = (double) activityMinutes/activityGoal;
        }
        return progress;
    }

    /**
     * Getter for  recommendation data
     * @return Returns the recommendation data
     */
    public RecommendationData getRecommendations() {
        return recommendations;
    }

    /**
     * Getter for  insights data
     * @return Returns the insights data
     */
    public CommunityInsightsData getInsights() {
        return insights;
    }
}
