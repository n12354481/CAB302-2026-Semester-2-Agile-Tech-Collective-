package com.example.cab302project.MoodForm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckIn {

    private int checkinID;
    private int userID;
    private LocalDate checkinDate;

    private int emotionToday;
    private int sleep;
    private int water;
    private int studyStress;

    private List<String> moods;

    // Used when creating a new check-in
    public CheckIn(
            int userID,
            LocalDate checkinDate,
            int emotionToday,
            int sleep,
            int water,
            int studyStress,
            List<String> moods
    ) {
        this.userID = userID;
        this.checkinDate = checkinDate;
        this.emotionToday = emotionToday;
        this.sleep = sleep;
        this.water = water;
        this.studyStress = studyStress;
        this.moods = new ArrayList<>(moods);
    }

    // Used when loading a saved check-in from the database
    public CheckIn(
            int checkinID,
            int userID,
            LocalDate checkinDate,
            int emotionToday,
            int sleep,
            int water,
            int studyStress,
            List<String> moods
    ) {
        this.checkinID = checkinID;
        this.userID = userID;
        this.checkinDate = checkinDate;
        this.emotionToday = emotionToday;
        this.sleep = sleep;
        this.water = water;
        this.studyStress = studyStress;
        this.moods = new ArrayList<>(moods);
    }

    public int getCheckinID() {
        return checkinID;
    }

    public void setCheckinID(int checkinID) {
        this.checkinID = checkinID;
    }

    public int getUserID() {
        return userID;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public int getEmotionToday() {
        return emotionToday;
    }

    public int getSleep() {
        return sleep;
    }

    public int getWater() {
        return water;
    }

    public int getStudyStress() {
        return studyStress;
    }

    public List<String> getMoods() {
        return new ArrayList<>(moods);
    }
}