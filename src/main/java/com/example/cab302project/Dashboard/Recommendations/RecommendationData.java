package com.example.cab302project.Dashboard.Recommendations;

import com.example.cab302project.MoodForm.CheckIn;

import java.util.List;
import java.util.Map;

public class RecommendationData {
    private List<Map<String, Object>> activityData;
    private List<CheckIn> checkinData;

    public RecommendationData(List<Map<String, Object>> activityData, List<CheckIn> checkinData)
    {
        this.activityData = activityData;
        this.checkinData = checkinData;
    }

    public List<Map<String, Object>> getActivityData() {
        return activityData;
    }

    public List<CheckIn> getCheckinData() {
        return checkinData;
    }
}
