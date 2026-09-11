import com.example.cab302project.Settings.ISettingsDAO;
import com.example.cab302project.Settings.SettingsModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SettingsTestClass {
    private SettingsModel settings;
    private ISettingsDAO settingsDAO;
    @BeforeEach
    public void setUp() {

        settings = new SettingsModel(false, false, false, false, false, false);

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
    //Data


    //Profile

    //Common
    //        5) Preferences are saved - Settings remain after being saved
}