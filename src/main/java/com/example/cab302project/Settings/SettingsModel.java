package com.example.cab302project.Settings;

public class SettingsModel {
    private boolean communityParticipation;
    private boolean activityDataParticipation;
    private boolean checkinDataParticipation;
    private boolean AIPersonalise;
    private boolean AIActivityPersonalise;
    private boolean AICheckinPersonalise;


    public SettingsModel(boolean communityParticipation,
                         boolean activityDataParticipation,
                         boolean checkinDataParticipation,
                         boolean AIPersonalise,
                         boolean AIActivityPersonalise,
                         boolean AICheckinPersonalise)
    {
        this.communityParticipation = communityParticipation;
        this.activityDataParticipation = activityDataParticipation;
        this.checkinDataParticipation = checkinDataParticipation;
        this.AIPersonalise = AIPersonalise;
        this.AIActivityPersonalise = AIActivityPersonalise;
        this.AICheckinPersonalise = AICheckinPersonalise;
    }


    //Privacy
    public void setCommunityParticipation(boolean enabled) {
        communityParticipation = enabled;
    }

    public void setActivityDataParticipation(boolean enabled) {
        activityDataParticipation = enabled;
    }

    public void setCheckinDataParticipation(boolean enabled) {
        checkinDataParticipation = enabled;
    }

    public boolean isCommunityParticipation()
    {
        return communityParticipation;
    }

    public boolean isCommunityActivityParticipation()
    {
        return activityDataParticipation;
    }

    public boolean isCommunityCheckinParticipation()
    {
        return checkinDataParticipation;
    }

    public boolean contributeToCommunityStatistics() {
        return communityParticipation;
        //Need the user's email/ID to turn it off for that user.
        //
//        boolean okToCommunityStats = true;
//        if(okToCommunityStats)
//        {
//            return true;
//        }
//
//        return false;
    }

    public boolean contributeOnlyActivity() {
        return communityParticipation && activityDataParticipation;
//        boolean okToCommunityActivityStats = true;
//        if(contributeToCommunityStatistics() && okToCommunityActivityStats)
//        {
//            return true;
//        }
//        return false;
    }

    public boolean contributeOnlyCheckIn() {
        return communityParticipation && checkinDataParticipation;
//        boolean okToCommunityCheckInStats = true;
//        if(contributeToCommunityStatistics() && okToCommunityCheckInStats)
//        {
//            return true;
//        }
//        return false;
    }

    //AI Personalisation
    public void setAIPersonalisation(boolean enabled) { AIPersonalise = enabled; }

    public boolean AIPersonalisationEnabled() {
        return AIPersonalise;
    }


    public void setAIActivityPersonalisation(boolean enabled) {
        AIActivityPersonalise = enabled;
    }

    public boolean AIActivityPersonalisationEnabled() {
        return AIPersonalise && AIActivityPersonalise;
    }

    public void setAICheckinPersonalisation(boolean enabled) {
        AICheckinPersonalise = enabled;
    }

    public boolean AICheckinPersonalisationEnabled() {
        return AIPersonalise && AICheckinPersonalise;
    }

    //Data

    //Profile
}
