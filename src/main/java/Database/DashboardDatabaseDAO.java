package Database;

import Dashboard.DashboardModel;
import Dashboard.IDashboardDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class DashboardDatabaseDAO implements IDashboardDAO {
    private Connection connection;

    public DashboardDatabaseDAO()
    {
        connection = DatabaseConnection.getInstance();
    }

    @Override
    public int getWeeklyCheckInStreak(int userId, LocalDate startDate, LocalDate endDate)
    {
        String query = "SELECT COUNT(*) FROM checkin WHERE userId = ? AND checkin_date BETWEEN ? AND ?;" ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, startDate.toString());
            statement.setString(3, endDate.toString());
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return result.getInt(1);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int userActivitiesCompleted(int userId)
    {
        String query = "SELECT COUNT(activityID) FROM activity_log WHERE userId = ?;" ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return result.getInt(1);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public double averageSleep(int userId)
    {
        String query = "SELECT AVG(sleep) FROM checkin WHERE userId = ? AND sleep IS NOT NULL;" ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return result.getDouble(1);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0.0;
    }

    @Override
    public double averageStudyStress(int userId)
    {
        String query = "SELECT AVG(study_stress) FROM checkin WHERE userId = ? AND study_stress IS NOT NULL;" ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return result.getDouble(1);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0.0;
    }

    @Override
    public int totalActivityMinutes(int userId)
    {
        String query = "SELECT COALESCE(SUM(minutes), 0) FROM activity_log WHERE userId = ?;" ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return result.getInt(1);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int userActivityGoal(int userId)
    {
        String query = "SELECT COALESCE(SUM(a.goal), 0) FROM activity_log al JOIN activity a ON al.activityID = a.activityID WHERE al.userId = ?;" ;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return result.getInt(1);

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }
}
