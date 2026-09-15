package CheckIn;

import Database.DatabaseConnection;
import Database.DatabaseSchema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ResetMoodTables {

    public static void main(String[] args) {

        Connection connection =
                DatabaseConnection.getInstance();

        try (Statement statement =
                     connection.createStatement()) {

            statement.execute(
                    "DROP TABLE IF EXISTS checkin_mood"
            );

            statement.execute(
                    "DROP TABLE IF EXISTS mood"
            );

            statement.execute(
                    "DROP TABLE IF EXISTS checkin"
            );

            System.out.println(
                    "Old mood/check-in tables removed."
            );

            DatabaseSchema.createAll(connection);

            System.out.println(
                    "New mood/check-in tables created."
            );

        } catch (SQLException e) {

            System.err.println(
                    "Unable to reset tables: "
                            + e.getMessage()
            );
        }
    }
}