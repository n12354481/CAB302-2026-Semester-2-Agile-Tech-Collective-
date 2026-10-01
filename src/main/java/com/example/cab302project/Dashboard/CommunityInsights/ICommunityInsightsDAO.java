package com.example.cab302project.Dashboard.CommunityInsights;

/**
 * An interface which enforces the DAO methods needed for the community insights section.
 */
public interface ICommunityInsightsDAO {
    int getParticipatingUserCount();
    double getAverageSleep();
    double getAverageStudyStress();
    double getAverageWater();
    double getAverageEmotion();
    int getTotalActivityMinutes();
    String getMostPopularActivityCategory();
    String getMostPopularMood();
}
