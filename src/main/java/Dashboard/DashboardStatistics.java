package Dashboard;

/**
 * This class will be used for calculating the community quotes.
 */
public class DashboardStatistics {
    private int totalUsers;
    private int totalActivities;
    private int totalCheckins;

    private double sleepStressPercentage;
    //More stat stuff...

    public DashboardStatistics(int totalUsers, int totalActivities, int totalCheckins, double sleepStressPercentage)
    {
        this.totalUsers = totalUsers;
        this.totalActivities = totalActivities;
        this.totalCheckins = totalCheckins;
        this.sleepStressPercentage = sleepStressPercentage;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getTotalAcitivities() {
        return totalActivities;
    }

    public int getTotalCheckins() {
        return totalCheckins;
    }

    public double getSleepStressPercentage() {
        return sleepStressPercentage;
    }

}
