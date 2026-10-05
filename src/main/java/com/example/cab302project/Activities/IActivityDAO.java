package com.example.cab302project.Activities;

import java.util.List;

public interface IActivityDAO {
    Activity getActivityById(int id);
    int insert(Activity activity);
    Activity findByName(String name);
    Activity getOrCreateActivity(Activity activity);
    List<Activity> getAllActivities();
    List<Activity> getActivitiesByCategory(String category);
    List<String> getCategories();
    void delete(int id);
    void addDefaultActivities();

    int insertActivityLog(int userId, int activityId, String logDate, int minutes);
    List<int[]> getActivityLogs(int userId);
    void deleteActivityLog(int logId, int userId);
}
