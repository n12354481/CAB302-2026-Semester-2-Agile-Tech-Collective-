package com.example.cab302project.Settings;

import java.time.LocalDate;

/**
 * This class aims to represent the main logic used for the Setting's feature in the app.
 */
public class SettingsModel {
    private boolean communityParticipation;
    private boolean activityDataParticipation;
    private boolean checkinDataParticipation;
    private boolean AIPersonalise;
    private boolean AIActivityPersonalise;
    private boolean AICheckinPersonalise;

    /**
     * Constructs a new Settings with the chosen values for the setting's pages.
     * @param communityParticipation: Boolean which allows the user to be included in the community statistics.
     * @param activityDataParticipation: Boolean which allows the user to be included in the community activity statistics.
     * @param checkinDataParticipation: Boolean which allows the user to be included in the community checkin statistics.
     * @param AIPersonalise: Boolean which allows the user to choose whether AI recommendations are allowed.
     * @param AIActivityPersonalise: Boolean which allows the user to choose whether AI recommendations about activities are allowed.
     * @param AICheckinPersonalise: Boolean which allows the user to choose whether AI recommendations about checkin are allowed.
     */
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

    /**
     * Setter for the community participation
     * @param enabled: boolean for checking if community participation is enabled.
     */
    public void setCommunityParticipation(boolean enabled) {
        communityParticipation = enabled;
    }

    /**
     * Setter for the community activity participation
     * @param enabled: boolean for checking if community activity participation is enabled.
     */
    public void setActivityDataParticipation(boolean enabled) {
        activityDataParticipation = enabled;
    }

    /**
     * Setter for the community checkin participation
     * @param enabled: boolean for checking if community checkin participation is enabled.
     */
    public void setCheckinDataParticipation(boolean enabled) {
        checkinDataParticipation = enabled;
    }

    /**
     * Getter for the community participation.
     * @return returns the community participation variable.
     */
    public boolean isCommunityParticipation()
    {
        return communityParticipation;
    }

    /**
     * Getter for the community activity participation.
     * @return returns the community activity participation variable.
     */
    public boolean isCommunityActivityParticipation()
    {
        return activityDataParticipation;
    }

    /**
     * Getter for the community checkin participation.
     * @return returns the community checkin participation variable.
     */
    public boolean isCommunityCheckinParticipation()
    {
        return checkinDataParticipation;
    }

    /**
     * Method which checks if the user allowed community participation.
     * @return returns the community participation variable.
     */
    public boolean contributeToCommunityStatistics() {
        return communityParticipation;
    }

    /**
     * Method which checks if the user allowed community activity participation.
     * @return returns true if both the community participation variable and the community activity participation variable are enabled.
     */
    public boolean contributeOnlyActivity() {
        return communityParticipation && activityDataParticipation;
    }

    /**
     * Method which checks if the user allowed community checkin participation.
     * @return returns true if both the community participation variable and the community checkin participation variable are enabled.
     */
    public boolean contributeOnlyCheckIn() {
        return communityParticipation && checkinDataParticipation;
    }


    //AI Personalisation

    /**
     * Setter for AI Personalise variable
     * @return returns AI personalise variable
     */
    public void setAIPersonalisation(boolean enabled) { AIPersonalise = enabled; }

    /**
     * Method which checks if the user allowed AI personalisation.
     * @return returns AI personalise variable
     */
    public boolean AIPersonalisationEnabled() {
        return AIPersonalise;
    }

    /**
     * Setter for AI Activity Personalise variable
     * @return returns AI activity personalise variable
     */
    public void setAIActivityPersonalisation(boolean enabled) {
        AIActivityPersonalise = enabled;
    }

    /**
     * Method which checks if the user allowed AI activity personalisation.
     * @return returns AI activity personalise variable
     */
    public boolean AIActivityPersonalisationEnabled() {
        return AIPersonalise && AIActivityPersonalise;
    }

    /**
     * Setter for AI Checkin Personalise variable
     * @return returns AI checkin ersonalise variable
     */
    public void setAICheckinPersonalisation(boolean enabled) {
        AICheckinPersonalise = enabled;
    }

    /**
     * Method which checks if the user allowed AI checkin personalisation.
     * @return returns AI checkin personalise variable
     */
    public boolean AICheckinPersonalisationEnabled() {
        return AIPersonalise && AICheckinPersonalise;
    }

}
