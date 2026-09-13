package com.example.cab302project.Activities;

public class Activity {
    private final int activityID;
    private final String name;
    private final String category;
    private final String description;
    private final int goal;
    private final String imageFile;

    public Activity(int activityID, String name, String category, String description, int goal, String imageFile) {
        this.activityID = activityID;
        this.name = name;
        this.category = category;
        this.description = description;
        this.goal = goal;
        this.imageFile = imageFile;
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
    public String getImageFile() { return imageFile; }
}
