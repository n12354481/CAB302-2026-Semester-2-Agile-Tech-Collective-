package com.example.cab302project.Authentication;

import com.example.cab302project.Database.DatabaseConnection;
import com.example.cab302project.Database.DatabaseSchema;
import com.example.cab302project.Database.DatabaseUserDAO;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDAOTest {

    public static void main(String[] args) {

        // Get connection to contacts.db
        Connection connection = DatabaseConnection.getInstance();

        // Create tables for this test
        try {
            DatabaseSchema.createAll(connection);
        } catch (SQLException e) {
            System.err.println(
                    "Could not create database tables: "
                            + e.getMessage()
            );
            return;
        }

        DatabaseUserDAO dao = new DatabaseUserDAO();

        User user = new User(
                "test@email.com",
                "testuser",
                "password123"
        );

        boolean registered = dao.registerUser(user);

        System.out.println("Registered: " + registered);

        System.out.println(
                "Email exists: "
                        + dao.emailExists("test@email.com")
        );

        System.out.println(
                "Username exists: "
                        + dao.usernameExists("testuser")
        );

        User loggedIn =
                dao.loginUser("testuser", "password123");

        if (loggedIn != null) {
            System.out.println(
                    "Login successful: "
                            + loggedIn.getUsername()
            );
        } else {
            System.out.println("Login failed");
        }
    }
}