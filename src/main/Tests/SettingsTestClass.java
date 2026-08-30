import com.example.cab302project.Settings.SettingsModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SettingsTestClass {
    private SettingsModel settings;
    @BeforeEach
    public void setUp() {
        settings = new SettingsModel();
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
        settings.setAIActivityPersonalisation(true);
        assertEquals(true, settings.AIActivityPersonalisationEnabled());
    }

    @Test
    public void testAICheckinPersonalisationEnabled()
    {
        settings.setAICheckinPersonalisation(true);
        assertEquals(true, settings.AICheckinPersonalisationEnabled());
    }

    //Data


    //Profile

    //Common
    //        5) Preferences are saved - Settings remain after being saved
}