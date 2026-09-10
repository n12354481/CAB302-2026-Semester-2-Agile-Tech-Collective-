package com.example.cab302project.Activities;

public class Activity {
    private int activityId;
    private String activityName;
    private String category;
    private String activityDescription;
    private int goal;
    private int points;

    // creates an activity before it has been saved to the database
    public Activity(String activityName, String category, String activityDescription, int goal, int points) {
        this.activityName = activityName;
        this.category = category;
        this.activityDescription = activityDescription;
        this.goal = goal;
        this.points = points;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getCategory() {
        return category;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public int getGoal() {
        return goal;
    }

    public int getPoints() {
        return points;
    }
}
