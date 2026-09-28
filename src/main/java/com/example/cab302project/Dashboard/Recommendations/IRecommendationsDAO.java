package com.example.cab302project.Dashboard.Recommendations;

import com.example.cab302project.MoodForm.CheckIn;

import java.util.List;
import java.util.Map;

public interface IRecommendationsDAO {
    List<Map<String, Object>> getRecentActivityData(int userId);
    List<CheckIn> getRecentCheckinData(int userId);
}
