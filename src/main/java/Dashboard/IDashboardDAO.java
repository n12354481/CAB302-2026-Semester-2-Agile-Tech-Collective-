package Dashboard;

import java.time.LocalDate;

/**
 * This interface implements the dashboard DAO methods needed for a functional dashboard.
 */
public interface IDashboardDAO {
    public int getWeeklyCheckInStreak(int userId, LocalDate startDate, LocalDate endDate);
    public int userActivitiesCompleted(int userId);
    public double averageSleep(int userId);
    public double averageStudyStress(int userId);
    public int totalActivityMinutes(int userId);
    public int userActivityGoal(int userId);
}
