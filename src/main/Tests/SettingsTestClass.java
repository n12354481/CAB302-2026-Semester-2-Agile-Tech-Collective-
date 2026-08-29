import com.example.cab302project.Settings.SettingsModel;
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
}


/*
Settings
    Privacy
        1) Community participation enabled - User's statistics are included in the community statistics
        2) Disabled community participation - User's stats are excluded from community participation
        3) Activity Data is disabled -  Only user's activity data is excluded from the community statistics
        4) Check-in Data is disabled - Only user's check-in data is excluded from the community statistics
        5) Preferences are saved - Settings remain after being saved

 */