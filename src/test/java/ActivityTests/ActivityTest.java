package java.ActivityTests;

import com.example.cab302project.Activities.Activity;
import com.example.cab302project.Database.DatabaseActivityDAO;
import com.example.cab302project.Database.DatabaseConnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * tests activity model and database methods used by activities feature
 */
public class ActivityTest {
   private Activity testActivity;
   private DatabaseActivityDAO activityDAO;
   private Connection connection;
   private boolean autoCommit;

   /**
    * creates objects required before each test is performed
    */
   @BeforeEach
    public void setUp() throws SQLException {

       testActivity = new Activity(
               "Swimming", "Fitness", "Swimming activity", 30);

       activityDAO = new DatabaseActivityDAO();
       connection = DatabaseConnection.getInstance();
       autoCommit = connection.getAutoCommit();
       connection.setAutoCommit(false); // stop test from being permanently saved
   }

   /**
    * rolls back database changes after each test
    */
   @AfterEach
    public void tearDown() throws SQLException {
       connection.rollback();
       connection.setAutoCommit(autoCommit);
   }

   // activity model tests

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

   // activity database test

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

   // activity log database test
   @Test
   public void testInsertActivityLog() {
      int activityId = activityDAO.insert(testActivity);
      int logId = activityDAO.insertActivityLog(1, activityId, "2026-10-06", 30);
      assertTrue(activityId > 0);
   }

   @Test
   public void testGetActivityLogs() {
      int activityId = activityDAO.insert(testActivity);
      activityDAO.insertActivityLog(1, activityId, "2026-10-06", 30);
      List<int[]> logs = activityDAO.getActivityLogs(1);
      assertFalse(logs.isEmpty());
   }

   @Test
   public void testGetActivityLogMinutes() {
      int activityId = activityDAO.insert(testActivity);
      activityDAO.insertActivityLog(1, activityId, "2026-10-06", 45);
      List<int[]> logs = activityDAO.getActivityLogs(1);
      assertFalse(logs.isEmpty());
      assertEquals(45, logs.get(0)[2]);
   }

   @Test
   public void testDeleteActivityLog() {
      int activityId = activityDAO.insert(testActivity);
      int logId = activityDAO.insertActivityLog(1, activityId, "2026-10-06", 30);
      activityDAO.deleteActivityLog(1, logId);

      List<int[]> beforeDelete = activityDAO.getActivityLogs(1);
      activityDAO.deleteActivityLog(logId, 1);

      List<int[]> afterDelete = activityDAO.getActivityLogs(1);
      assertEquals(beforeDelete.size() -1, afterDelete.size());
   }


}
