package AuthenticationTests;

import com.example.cab302project.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordHashTest {

    public static void main(String[] args) {

        Connection connection = DatabaseConnection.getInstance();

        String query =
                "SELECT userID, email, username, password FROM users";

        try (PreparedStatement statement =
                     connection.prepareStatement(query);

             ResultSet result =
                     statement.executeQuery()) {

            System.out.println("=== USERS TABLE ===");

            boolean usersFound = false;

            while (result.next()) {

                usersFound = true;

                int userID =
                        result.getInt("userID");

                String email =
                        result.getString("email");

                String username =
                        result.getString("username");

                String storedPassword =
                        result.getString("password");

                System.out.println();
                System.out.println("User ID: " + userID);
                System.out.println("Email: " + email);
                System.out.println("Username: " + username);
                System.out.println(
                        "Stored password: " + storedPassword
                );

                if (storedPassword != null
                        && storedPassword.startsWith("120000:")) {

                    System.out.println(
                            "Result: Password appears to be hashed."
                    );

                } else {

                    System.out.println(
                            "Result: Password is NOT using the new hash format."
                    );
                }
            }

            if (!usersFound) {
                System.out.println(
                        "No users were found in the database."
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Unable to read users table: "
                            + e.getMessage()
            );
        }
    }
}