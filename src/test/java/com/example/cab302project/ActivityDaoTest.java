package com.example.cab302project;

import com.example.cab302project.Activities.Activity;
import com.example.cab302project.Database.DatabaseActivityDAO;
import com.example.cab302project.Database.DatabaseConnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class ActivityDaoTest {
   private Activity testActivity;
   private DatabaseActivityDAO activityDAO;
   private Connection connection;
   private boolean autoCommit;

   @BeforeEach
    public void setUp() throws SQLException {

       testActivity = new Activity(
               "Swimming", "Fitness", "Swimming activity", 30, null);

       activityDAO = new DatabaseActivityDAO();
       connection = DatabaseConnection.getInstance();
       autoCommit = connection.getAutoCommit();
       connection.setAutoCommit(false);
   }

   @AfterEach
    public void tearDown() throws SQLException {
       connection.rollback();
       connection.setAutoCommit(autoCommit);
   }

   @Test
    public void testActivityName() {
       assertEquals("Swimming", testActivity.getName());
   }

   @Test
    public void testActivityCategory() {
       assertEquals("Fitness", testActivity.getCategory());
   }

   @Test
    public void testActivityDescription() {
       assertEquals("Swimming activity", testActivity.getDescription());
   }

   @Test
    public void testActivityGoal() {
       assertEquals(30, testActivity.getGoal());
   }

   @Test
   public void testInsertActivity() {
      int id = activityDAO.insert(testActivity);
      assertTrue(id > 0);
   }

   @Test
   public void testFindActivityByName() {
      activityDAO.insert(testActivity);
      Activity found = activityDAO.findByName(testActivity.getName());
      assertNotNull(found);
      assertEquals(testActivity.getName(), found.getName());
   }

   @Test
   public void testGetActivityById() {
      int id = activityDAO.insert(testActivity);
      Activity found = activityDAO.getActivityById(id);
      assertNotNull(found);
      assertEquals(testActivity.getName(), found.getName());
   }

   @Test
   public void testGetOrCreateActivityById() {
      Activity found = activityDAO.getOrCreateActivity(testActivity);
      assertNotNull(found);
      assertEquals(testActivity.getName(), found.getName());
   }

}
