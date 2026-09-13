package com.example.cab302project.Settings;

import com.example.cab302project.Authentication.IUserDAO;
import com.example.cab302project.Authentication.User;
import com.example.cab302project.Database.DatabaseSettingsDAO;
import com.example.cab302project.Database.DatabaseUserDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
    public  void testCommunityParticipationEnabled() {
        settings.setCommunityParticipation(true);
        assertEquals(true, settings.contributeToCommunityStatistics());
    }

    @Test
    public  void testCommunityParticipationDisabled() {
        settings.setCommunityParticipation(false);
        assertEquals(false, settings.contributeToCommunityStatistics());
    }

    @Test
    public  void testActivityDataDisabled() {
        settings.setCommunityParticipation(true);
        settings.setActivityDataParticipation(false);
        assertEquals(false, settings.contributeOnlyActivity());
    }


    //AI Personalisation
    @Test
    public void testAIPersonalisationEnabled()
    {
        settings.setAIPersonalisation(true);
        assertEquals(true, settings.AIPersonalisationEnabled());
    }

    @Test
    public void testAIActivityPersonalisationEnabled()
    {
        settings.setAIPersonalisation(true);
        settings.setAIActivityPersonalisation(true);
        assertEquals(true, settings.AIActivityPersonalisationEnabled());
    }

    @Test
    public void testAICheckinPersonalisationEnabled()
    {
        settings.setAIPersonalisation(true);
        settings.setAICheckinPersonalisation(true);
        assertEquals(true, settings.AICheckinPersonalisationEnabled());
    }

    @Test
    public void testAIActivityPersonalisationDisabled()
    {
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
    public void testUsernameUpdate()
    {
        userDAO.updateUsername(4, "testingUpdated4");
        User result = userDAO.getUserId(4);
        assertEquals("testingUpdated4", result.getUsername());
    }

    @Test
    public void testEmailUpdate()
    {
        userDAO.updateEmail(4, "testingUpdated4@gmail.com");
        User result = userDAO.getUserId(4);
        assertEquals("testingUpdated4@gmail.com", result.getEmail());
    }

    @Test
    public void testPasswordUpdate()
    {
        userDAO.updatePassword(4, "passwordUpdated1");
        User result = userDAO.getUserId(4);
        assertEquals("passwordUpdated1", result.getPassword());
    }

    //Data
}