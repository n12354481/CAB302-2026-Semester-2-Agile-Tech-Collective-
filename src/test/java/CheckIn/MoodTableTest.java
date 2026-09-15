package CheckIn;

import Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MoodTableTest {

    public static void main(String[] args) {

        Connection connection =
                DatabaseConnection.getInstance();

        String query =
                "SELECT moodID, mood_name "
                        + "FROM mood "
                        + "ORDER BY moodID";

        try (Statement statement =
                     connection.createStatement();

             ResultSet result =
                     statement.executeQuery(query)) {

            System.out.println("=== MOODS ===");

            while (result.next()) {

                int moodID =
                        result.getInt("moodID");

                String moodName =
                        result.getString("mood_name");

                System.out.println(
                        moodID + " - " + moodName
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Unable to read moods: "
                            + e.getMessage()
            );
        }
    }
}