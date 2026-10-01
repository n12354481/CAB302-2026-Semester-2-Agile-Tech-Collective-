package com.example.cab302project.Dashboard.Recommendations;

import com.example.cab302project.MoodForm.CheckIn;

import java.util.List;
import java.util.Map;

/**
 * This class aims to define the attributes specifically needed for generating personalised recommendations for users.
 */
public class RecommendationData {
    private List<Map<String, Object>> activityData;
    private List<CheckIn> checkinData;

    /**
     * This constructs the activity data and the checkin data needed for generating the Ollama's response.
     * @param activityData: The physical wellbeing data of the user.
     * @param checkinData: The mental wellbeing data of the user.
     */
    public RecommendationData(List<Map<String, Object>> activityData, List<CheckIn> checkinData)
    {
        this.activityData = activityData;
        this.checkinData = checkinData;
    }

    /**
     * Getter for the activity data.
     * @return: Returns the activity data.
     */
    public List<Map<String, Object>> getActivityData() {
        return activityData;
    }

    /**
     * Getter for the checkin data.
     * @return: Returns the checkin data.
     */
    public List<CheckIn> getCheckinData() {
        return checkinData;
    }
}
