package com.example.cab302project.Settings;

public class SettingsModel {
    private boolean communityParticipation;
    private boolean activityDataParticipation;
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

    public void setCommunityParticipation(boolean enabled)
    {
        communityParticipation = enabled;
    }

    public void setActivityDataParticipation(boolean enabled)
    {
        communityParticipation = enabled;
    }
    public boolean contributeOnlyActivity()
    {
        return communityParticipation;
//        boolean okToCommunityActivityStats = true;
//        if(contributeToCommunityStatistics() && okToCommunityActivityStats)
//        {
//            return true;
//        }
//        return false;
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
