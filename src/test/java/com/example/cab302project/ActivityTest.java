package com.example.cab302project;

import com.example.cab302project.Activities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ActivityTest {

    private Activity activity;

    @BeforeEach
    public void setUp(){
        activity = new Activity( 1, "Swimming", "Fitness", "Swimming is a full body physical activity.", 30, null);
    }

    @Test
    public void testGetActivityID(){ assertEquals(1, activity.getActivityID()); }

    @Test
    public void testGetName() { assertEquals("Swimming", activity.getName()); }

    @Test
    public void testGetCategory() { assertEquals("Fitness", activity.getCategory()); }

    @Test
    public void testGetDescription() { assertEquals("Swimming is a full body physical activity.", activity.getDescription()); }

    @Test
    public void testGetGoal() { assertEquals(30, activity.getGoal()); }

    @Test
    public void testGetImageFile() { assertNull(activity.getImageFile()); }
}
