package com.example.cab302project.Database;

import com.example.cab302project.Activities.Activity;
import com.example.cab302project.Activities.IActivityDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * handles database operations related to activities
 *
 * used to insert activities, retrieve saved activities and check whether
 * an activity already exists in database
 */

public class DatabaseActivityDAO implements IActivityDAO {
    private final Connection connection;

    public DatabaseActivityDAO() {
        this.connection = DatabaseConnection.getInstance();
    }
    // converts a database result into an activity object
    private static Activity marshallActivity(ResultSet rs) throws SQLException {
        int id = rs.getInt("activityID");
        String name = rs.getString("activity_name");
        String category = rs.getString("category");
        String description = rs.getString("activity_description");
        int goal = rs.getInt("goal");

        return new Activity(id, name, category, description, goal);
    }

    // finds activity using database id
    @Override
    public Activity getActivityById(int id) {
        String query = "SELECT * FROM activity WHERE activityID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return marshallActivity(rs);
            }
            return null;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * inserts a new activity into db, returns the generated or -1 on failure
     */
    @Override
    public int insert(Activity activity) {
        int insertId = -1;

        String sql = "INSERT INTO activity " + "(activity_name, category, activity_description, goal) " + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getCategory());
            stmt.setString(3, activity.getDescription());
            stmt.setInt(4, activity.getGoal());

            int affectedRows = stmt.executeUpdate();

            // retrieves id created by database
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                    if (generatedKeys.next()) {
                        insertId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return insertId;
    }

    /**
     * searches for activity using its name
     *
     * @param name activity name to search for
     * @return matching activity or null if it does not exist
     */

    @Override
    public Activity findByName(String name) {
        String query = "SELECT * FROM activity WHERE activity_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return marshallActivity(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // returns existing activity or inserts new one if it doesn't exist yet
    @Override
    public Activity getOrCreateActivity(Activity activity) {
        Activity existing = findByName(activity.getName());
        if (existing != null && existing.getCategory().equalsIgnoreCase(activity.getCategory())) {
            return existing;
        }

        int id = insert(activity);

        return new Activity(id, activity.getName(), activity.getCategory(), activity.getDescription(), activity.getGoal());
    }

    @Override
    public List<String> getCategories() {
        return List.of("Fitness", "Social", "Others");
    }

    @Override
    public List<Activity> getAllActivities() {
        List<Activity> savedActivities = new ArrayList<>();
        List<String> categories = getCategories();

        String query = "SELECT * FROM activity " + "WHERE category IN (?, ?, ?) " + "ORDER BY activityID";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, categories.get(0));
            statement.setString(2, categories.get(1));
            statement.setString(3, categories.get(2));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    savedActivities.add(marshallActivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return savedActivities;
    }

    @Override
    public List<Activity> getActivitiesByCategory(String category) {
        List<Activity> categoryActivities = new ArrayList<>();
        String query = "SELECT * FROM activity " + "WHERE category = ? " + "ORDER BY activityID";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, category);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    categoryActivities.add(marshallActivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryActivities;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM activity WHERE activityID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addDefaultActivities() {
        addDefaultActivity(
                "Swimming",
                "Fitness",
                "Swimming is a full body physical activity that can help improve fitness while also giving you a break from studying and sitting for long periods of time. It can be done at your own pace, whether you want to swim a few relaxed laps or have more active workout.",
                30
        );
        addDefaultActivity(
                "Jogging",
                "Fitness",
                "Jogging is a simple way to stay physically active and take some time away from studying or sitting at a desk. You can jog around campus, your neighbourhood or a nearby park at a pace that feels comfortable. It can also be a good way to clear your mind and have a break after spending a long time working on university tasks.",
                30
        );
        addDefaultActivity(
                "Tennis",
                "Fitness",
                "Tennis is an active sport that can help improve fitness, coordination and concentration. It can be played casually or competitively with another person, making it a good way to exercise while also spending time with friends and taking a break from studying.",
                30
        );
        addDefaultActivity(
                "Study with a friend",
                "Social",
                "Studying with a friend gives you the chance to work through university content together, discuss difficult topics and help each other when something is confusing. It can make studying feel less isolating and can also help you stay motivated and focused on what you need to complete.",
                30
        );
        addDefaultActivity(
                "Attend a workshop",
                "Social",
                "Attend a workshop to learn something new or develop skills outside of your normal classes. Workshops can give you practical experience, introduce you to different topics and provide an opportunity to meet other students who may have similar academic or career interests.",
                30
        );
        addDefaultActivity(
                "Join a STEM society event",
                "Social",
                "Take part in an event organised by a STEM-related student society at university. These events can be a good way to meet students with similar interests, learn more about different areas of STEM and get involved with the university community outside of classes.",
                30
        );
        addDefaultActivity(
                "Meditation",
                "Others",
                "Meditation is a simple activity where you take some time away from studying and other distractions to slow down and focus on the present moment. Even a short meditation session can give you some quiet time to relax, clear your mind and reset before continuing with your day.",
                30
        );
        addDefaultActivity(
                "Review lecture notes",
                "Others",
                "Spend some time going back through notes from your recent lectures or tutorials to refresh your understanding of the content. Regularly reviewing notes can help you identify topics you are unsure about and avoid leaving all of your revision until right before an assessment or exam.",
                30
        );
        addDefaultActivity(
                "Coding Practice",
                "Others",
                "Spend some time practising programming outside of your required classwork. You could work through coding exercises, practise concepts you found difficult in class or experiment with a small problem. Regular practice can help you become more comfortable with programming and problem solving over time.",
                30
        );
        addDefaultActivity(
                "Reading",
                "Others",
                "Take some time to read something you enjoy outside of your usual university work. This could be a novel, short story, magazine or another topic that interests you. Reading can be a relaxing way to spend some time away from assignments, coding and screens.",
                30
        );
        addDefaultActivity(
                "Gym workout",
                "Fitness",
                "Complete a gym workout based on your own fitness level and goals. This could include strength training, cardio or a combination of different exercises. Going to the gym can help you stay physically active, especially when a lot of your university work involves sitting at a desk or computer.",
                30
        );
        addDefaultActivity(
                "Go for a walk",
                "Others",
                "Take a break from your desk and go for a walk around campus, your neighbourhood or somewhere outdoors. Walking is a simple way to get some movement into your day and can give you a chance to clear your head after spending a long time studying or working on an assignment.",
                30
        );
        addDefaultActivity(
                "University club event",
                "Social",
                "Attend an event organised by one of the university's student clubs or societies. It is an opportunity to take a break from academic work, try something different and meet other students who share similar interests. It can also help you feel more involved in university life outside of classes.",
                30
        );
        addDefaultActivity(
                "Lunch with a friend",
                "Social",
                "Take some time away from studying to have lunch with a friend or classmate. It gives you a chance to catch up, talk about things outside of university work and have a proper break during a busy day instead of spending the whole day studying by yourself.",
                30
        );
    }

    @Override
    public int insertActivityLog(int userId, int activityId, String logDate, int minutes) {
        String sql = "INSERT INTO activity_log " +
                "(userID, activityID, log_date, minutes) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setInt(2, activityId);
            statement.setString(3, logDate);
            statement.setInt(4, minutes);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    @Override
    public List<int[]> getActivityLogs(int userId) {
        List<int[]> logs = new ArrayList<>();

        String sql = "SELECT logID, activityID, minutes " +
                "FROM activity_log " +
                "WHERE userID = ? " +
                "ORDER BY logID DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int[] log = {
                            rs.getInt("logID"), rs.getInt("activityID"), rs.getInt("minutes")
                    };
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return logs;
    }

    @Override
    public void deleteActivityLog(int logId, int userId) {
        String sql = "DELETE FROM activity_log " + "WHERE logID = ? AND userID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, logId);
            statement.setInt(2, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addDefaultActivity(String name, String category, String description, int goal) {
        if (findByName(name) != null) {
            return;
        }
        Activity activity = new Activity(name, category, description, goal);
        insert(activity);
    }
}


