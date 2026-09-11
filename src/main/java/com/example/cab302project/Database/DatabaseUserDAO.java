package com.example.cab302project.Database;

import com.example.cab302project.Authentication.IUserDAO;
import com.example.cab302project.Authentication.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUserDAO implements IUserDAO {

    private final Connection connection;

    public DatabaseUserDAO() {
        connection = DatabaseConnection.getInstance();
    }

    @Override
    public boolean registerUser(User user) {

        String query =
                "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());

            statement.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.err.println("Unable to register user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User loginUser(String username, String password) {

        String query =
                "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                return new User(
                        result.getInt("userID"),
                        result.getString("email"),
                        result.getString("username"),
                        result.getString("password")
                );
            }

        } catch (SQLException e) {
            System.err.println("Unable to login: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean emailExists(String email) {

        String query =
                "SELECT userID FROM users WHERE email = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, email);

            ResultSet result = statement.executeQuery();

            return result.next();

        } catch (SQLException e) {
            System.err.println("Unable to check email: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean usernameExists(String username) {

        String query =
                "SELECT userID FROM users WHERE username = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            return result.next();

        } catch (SQLException e) {
            System.err.println("Unable to check username: " + e.getMessage());
            return false;
        }
    }
}