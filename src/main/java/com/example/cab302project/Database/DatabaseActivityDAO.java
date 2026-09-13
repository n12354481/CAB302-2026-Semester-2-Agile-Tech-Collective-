package com.example.cab302project.Database;

import com.example.cab302project.Activities.Activity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivityDAO {
    private final Connection connection = DatabaseConnection.getInstance();

    public List<Activity> LoadActivities() {
        String query = "SELECT id, name, category, description, goal, image_file FROM activity";
        List<Activity> activities = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                Activity ac = marshallActivity(rs);
                activities.add(ac);
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // marshall an activity record from resultSet
    private static Activity marshallActivity(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String category = rs.getString("category");
        String description = rs.getString("description");
        int goal = rs.getInt("goal");
        String imageFile = rs.getString("image_file");

        Activity ac = new Activity(id, name, category, description, goal, imageFile);
        return ac;
    }

    public Activity findById(int id) {
        String query = "SELECT * FROM activity WHERE id = ?";
        try{
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return marshallActivity(rs);
            }
            else return null;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(Activity activity)
    {
        int insertedId = -1;
        String sql = "INSERT INTO activity (name, category, description, goal, image_file) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getCategory());
            stmt.setString(3, activity.getDescription());
            stmt.setInt(4, activity.getGoal());
            stmt.setString(5, activity.getImageFile());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // SQLite rowids are 64-bit integers, getLong would be safer
                        insertedId = generatedKeys.getInt(1);
                        System.out.println("Successfully inserted! New ID: " + insertedId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertedId;
    }

    public Activity findByName(String name)
    {
        String query = "SELECT * FROM activity WHERE name = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return marshallActivity(rs);
            }
            else  return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(int id)
    {
        String query = "DELETE FROM activity WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // add initial test data to an empty database
    public void AddInitialData() {
        List<Activity> test = prepareTestData();
        for (Activity ac : test) {
            insert(ac);
        }
    }

    // prepares test data for an empty database
    private List<Activity> prepareTestData() {
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("Swimming", "Fitness",
                "Swimming is a full body physical activity that can help improve fitness while also giving you a break from studying and sitting for long periods of time. It can be done at your own pace, whether you want to swim a few relaxed laps or have more active workout.",
                30, "Swimming.jpg"));

        activities.add(new Activity("Jogging", "Fitness",
                "Jogging is a simple way to stay physically active and take some time away from studying or sitting at a desk. You can jog around campus, your neighbourhood or a nearby park at a pace that feels comfortable. It can also be a good way to clear your mind and have a break after spending a long time working on university tasks.",
                30, null));

        activities.add(new Activity("Tennis", "Fitness",
                "Tennis is an active sport that can help improve fitness, coordination and concentration. It can be played casually or competitively with another person, making it a good way to exercise while also spending time with friends and taking a break from studying.",
                30, "Tennis.png"));

        activities.add(new Activity("Study with a friend", "Social",
                "Studying with a friend gives you the chance to work through university content together, discuss difficult topics and help each other when something is confusing. It can make studying feel less isolating and can also help you stay motivated and focused on what you need to complete.",
                30, null));

        activities.add(new Activity("Attend a workshop", "Social",
                "Attend a workshop to learn something new or develop skills outside of your normal classes. Workshops can give you practical experience, introduce you to different topics and provide an opportunity to meet other students who may have similar academic or career interests.",
                30, null));

        activities.add(new Activity("Join a STEM society event", "Social",
                "Take part in an event organised by a STEM-related student society at university. These events can be a good way to meet students with similar interests, learn more about different areas of STEM and get involved with the university community outside of classes.",
                30, null));

        activities.add(new Activity("Meditation", "Others",
                "Meditation is a simple activity where you take some time away from studying and other distractions to slow down and focus on the present moment. Even a short meditation session can give you some quiet time to relax, clear your mind and reset before continuing with your day.",
                30, null));

        activities.add(new Activity("Review lecture notes", "Others",
                "Spend some time going back through notes from your recent lectures or tutorials to refresh your understanding of the content. Regularly reviewing notes can help you identify topics you are unsure about and avoid leaving all of your revision until right before an assessment or exam.",
                30, null));

        activities.add(new Activity("Coding Practice", "Others",
                "Spend some time practising programming outside of your required classwork. You could work through coding exercises, practise concepts you found difficult in class or experiment with a small problem. Regular practice can help you become more comfortable with programming and problem solving over time.",
                30, null));

        activities.add(new Activity("Reading", "Others",
                "Take some time to read something you enjoy outside of your usual university work. This could be a novel, short story, magazine or another topic that interests you. Reading can be a relaxing way to spend some time away from assignments, coding and screens.",
                30, null));

        activities.add(new Activity("Gym workout", "Fitness",
                "Complete a gym workout based on your own fitness level and goals. This could include strength training, cardio or a combination of different exercises. Going to the gym can help you stay physically active, especially when a lot of your university work involves sitting at a desk or computer.",
                30, null));

        activities.add(new Activity("Go for a walk", "Others",
                "Take a break from your desk and go for a walk around campus, your neighbourhood or somewhere outdoors. Walking is a simple way to get some movement into your day and can give you a chance to clear your head after spending a long time studying or working on an assignment.",
                30, null));

        activities.add(new Activity("University club event", "Social",
                "Attend an event organised by one of the university's student clubs or societies. It is an opportunity to take a break from academic work, try something different and meet other students who share similar interests. It can also help you feel more involved in university life outside of classes.",
                30, null));

        activities.add(new Activity("Lunch with a friend", "Social",
                "Take some time away from studying to have lunch with a friend or classmate. It gives you a chance to catch up, talk about things outside of university work and have a proper break during a busy day instead of spending the whole day studying by yourself.",
                30, null));

        return activities;
    }
}

