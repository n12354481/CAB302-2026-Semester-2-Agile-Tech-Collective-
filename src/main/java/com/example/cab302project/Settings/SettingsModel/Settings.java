package com.example.cab302project.Settings.SettingsModel;

public class Settings {
    //Privacy
    public boolean contributeToCommunityStatistics()
    {
        boolean okToCommunityStats = true;
        if(okToCommunityStats)
        {
            return true;
        }

        return false;
    }

    public boolean contributeOnlyActivity()
    {
        boolean okToCommunityActivityStats = true;
        if(contributeToCommunityStatistics() && okToCommunityActivityStats)
        {
            return true;
        }
        return false;
    }

    public boolean contributeOnlyCheckIn()
    {
        boolean okToCommunityCheckInStats = true;
        if(contributeToCommunityStatistics() && okToCommunityCheckInStats)
        {
            return true;
        }
        return false;
    }
}
