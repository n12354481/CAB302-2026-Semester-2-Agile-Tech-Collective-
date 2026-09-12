package com.example.cab302project.Activities;

public class Activity {
    private final int activityID;
    private final String name;
    private final String category;
    private final String description;
    private final int goal;
    private final String imageLocation;

    public Activity(int activityID, String name, String category, String description, int goal, String imageLocation) {
        this.activityID = activityID;
        this.name = name;
        this.category = category;
        this.description = description;
        this.goal = goal;
        this.imageLocation = imageLocation;
    }

    public int getActivityID() {
        return activityID;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public String getDescription() {
        return description;
    }
    public int getGoal() { return goal; }
    public String getImageLocation() { return imageLocation; }
}
