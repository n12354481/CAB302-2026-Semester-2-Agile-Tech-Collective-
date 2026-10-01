package SettingsTests;

import com.example.cab302project.Activities.Activity;
import com.example.cab302project.Authentication.IUserDAO;
import com.example.cab302project.Authentication.User;
import com.example.cab302project.Database.*;

import com.example.cab302project.MoodForm.ICheckInDAO;
import com.example.cab302project.Settings.ISettingsDAO;
import com.example.cab302project.Settings.SettingsModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This class is the test class for the Settings feature.
 */
public class SettingsTestClass {
    private SettingsModel settings;
    private ISettingsDAO settingsDAO;
    private IUserDAO userDAO;
    private User user;

    @BeforeEach
    public void setUp() {

        settings = new SettingsModel(false, false, false, false, false, false);
        user = new User(4, "test2@example.com", "testUser", "password123");
        userDAO = new DatabaseUserDAO();
        settingsDAO = new DatabaseSettingsDAO();
    }

    //Privacy
    @Test
    public void testCommunityParticipationEnabled() {
        settings.setCommunityParticipation(true);
        assertEquals(true, settings.contributeToCommunityStatistics());
    }

    @Test
    public void testCommunityParticipationDisabled() {
        settings.setCommunityParticipation(false);
        assertEquals(false, settings.contributeToCommunityStatistics());
    }

    @Test
    public void testActivityDataDisabled() {
        settings.setCommunityParticipation(true);
        settings.setActivityDataParticipation(false);
        assertEquals(false, settings.contributeOnlyActivity());
    }


    //AI Personalisation
    @Test
    public void testAIPersonalisationEnabled() {
        settings.setAIPersonalisation(true);
        assertEquals(true, settings.AIPersonalisationEnabled());
    }

    @Test
    public void testAIActivityPersonalisationEnabled() {
        settings.setAIPersonalisation(true);
        settings.setAIActivityPersonalisation(true);
        assertEquals(true, settings.AIActivityPersonalisationEnabled());
    }

    @Test
    public void testAICheckinPersonalisationEnabled() {
        settings.setAIPersonalisation(true);
        settings.setAICheckinPersonalisation(true);
        assertEquals(true, settings.AICheckinPersonalisationEnabled());
    }

    @Test
    public void testAIActivityPersonalisationDisabled() {
        settings.setAIPersonalisation(false);
        settings.setAIActivityPersonalisation(true);
        assertEquals(false, settings.AIActivityPersonalisationEnabled());
    }


    @Test
    public void testSettingsCanBeSavedAndRetrieved() {
        int userId = 1;

        SettingsModel settings = new SettingsModel(true, true, false, true, false, true);
        settingsDAO.saveSettings(userId, settings);
        SettingsModel result = settingsDAO.getSettings(userId);
        assertEquals(true, result.isCommunityParticipation());
        assertEquals(false, result.isCommunityCheckinParticipation());
        assertEquals(true, result.isCommunityActivityParticipation());
    }

    //Profile
    @Test
    public void testUsernameUpdate() {
        userDAO.updateUsername(4, "testingUpdated4");
        User result = userDAO.getUserId(4);
        assertEquals("testingUpdated4", result.getUsername());
    }

    @Test
    public void testEmailUpdate() {
        userDAO.updateEmail(4, "testingUpdated4@gmail.com");
        User result = userDAO.getUserId(4);
        assertEquals("testingUpdated4@gmail.com", result.getEmail());
    }

    @Test
    public void testPasswordUpdate() {
        userDAO.updatePassword(4, "passwordUpdated1");
        User result = userDAO.getUserId(4);
        assertEquals("passwordUpdated1", result.getPassword());

    }

    //Data
    //This method tests wehther the activities data is actually deleted.
    @Test
    public void testDeleteActivitiesData() {
        Connection connection = DatabaseConnection.getInstance();
        DatabaseActivityDAO activityDAO = new DatabaseActivityDAO();
        int userId = 2;
        int activityID = activityDAO.insert(new Activity("Walking", "Walking", "I walked.", 300, null));

        String query = "INSERT INTO activity_log (userID, activityID, log_date, minutes) " +
                "VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement((query));
            statement.setInt(1, userId);
            statement.setInt(2, activityID);
            statement.setString(3, "2026-01-10");
            statement.setInt(4, 30);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String retrieving_activity = "SELECT * " + "WHERE user_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();
            assertNotNull(activityDAO.getActivityById(activityID));

            settingsDAO.deleteActivities(userId);
            assertNull(result.next());
            assertNull(activityDAO.getActivityById(activityID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This method tests whether the checkin data is actually deleted.
    @Test
    public void testDeleteCheckinData() {
        ICheckInDAO checkInDAO = new DatabaseCheckInDAO();
        int userId = 2;
        settingsDAO.deleteCheckin(userId);
        assertEquals(new ArrayList<>(), checkInDAO.getCheckInsForUser(2));
    }

    //This method checks that the user's account is deleted.
    @Test
    public void testDeleteAccount()
    {
        User test = new User(
                100000,
                "deleteTestForSettings@example.com",
                "deleteTestUserForSettings",
                "password123"
        );
        userDAO.registerUser(test);
        User user = userDAO.getUserId(100000);
        assertNotNull(userDAO.emailExists("deleteTestForSettings@example.com"));
        settingsDAO.deleteAccount(100000);
        assertNull(userDAO.getUserId(100000)); //Given userId.
    }

}