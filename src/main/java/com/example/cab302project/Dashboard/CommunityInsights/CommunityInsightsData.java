package com.example.cab302project.Dashboard.CommunityInsights;

/**
 * This class initiates the community data model which is specifically designed to get the relevant data for the insights section.
 */
public class CommunityInsightsData {
    private int participatingUsers;
    private double avgSleep;
    private double avgStudyStress;
    private double avgWater;
    private double avgEmotion;
    private int totalActivityMinutes;
    private String mostPopularActivity;
    private String mostPopularMood;

    /**
     *
     * @param participatingUsers: The number of users participating in the community section.
     * @param avgSleep: The average sleep per hours data for the participating users.
     * @param avgStudyStress: The average study stress per hours data for the participating users.
     * @param avgEmotion: The average emotion data for the participating users.
     * @param avgWater: The average water data for the participating users.
     * @param totalActivityMinutes: The total activities minutes data for the participating users.
     * @param mostPopularActivity: The most popular activity category for the participating users.
     * @param mostPopularMood: The most popular mood for the participating users.
     */
    public CommunityInsightsData(
            int participatingUsers,
            double avgSleep,
            double avgStudyStress,
            double avgEmotion,
            double avgWater,
            int totalActivityMinutes,
            String mostPopularActivity,
            String mostPopularMood
    ) {
        this.participatingUsers = participatingUsers;
        this.avgStudyStress = avgStudyStress;
        this.avgSleep = avgSleep;
        this.avgEmotion = avgEmotion;
        this.avgWater = avgWater;
        this.mostPopularActivity = mostPopularActivity;
        this.mostPopularMood = mostPopularMood;
        this.totalActivityMinutes = totalActivityMinutes;
    }

    /**
     * A method to get the participating users.
     * @return returns the participating users.
     */
    public int getParticipatingUsers() {
        return participatingUsers;
    }

    /**
     * A method to get the total activity minutes.
     * @return returns the activity minutes.
     */
    public int getTotalActivityMinutes() {
        return totalActivityMinutes;
    }

    /**
     * A method to get the average sleep data.
     * @return returns the average sleep.
     */
    public double getAvgSleep() {
        return avgSleep;
    }

    /**
     * A method to get the average study stress data.
     * @return returns the average study stress.
     */
    public double getAvgStudyStress()
    {
        return avgStudyStress;
    }

    /**
     * A method to get the average water data.
     * @return returns the average water.
     */
    public double getAvgWater()
    {
        return avgWater;
    }

    /**
     * A method to get the average emotion data.
     * @return returns the average emotion.
     */
    public double getAvgEmotion()
    {
        return avgEmotion;
    }

    /**
     * A method to get the most popular activity data.
     * @return returns the most popular activity.
     */
    public String getMostPopularActivity()
    {
        return mostPopularActivity;
    }

    /**
     * A method to get the most popular mood data.
     * @return returns the most popular mood.
     */
    public String getMostPopularMood()
    {
        return mostPopularMood;
    }
}
