package Database;

import Authentication.IUserDAO;
import Authentication.User;
import Authentication.PasswordUtils;

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
                "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    String storedPassword =
                            result.getString("password");

                    boolean passwordCorrect =
                            PasswordUtils.verifyPassword(
                                    password,
                                    storedPassword
                            );

                    if (passwordCorrect) {

                        return new User(
                                result.getInt("userID"),
                                result.getString("email"),
                                result.getString("username"),
                                storedPassword
                        );
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Unable to login: " + e.getMessage()
            );
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

    @Override
    public User getUserId(int userId)
    {
        String query =
                "SELECT * FROM users WHERE userId = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            ResultSet result = statement.executeQuery();

            if(result.next())
            {
                return new User(
                        result.getInt("userID"),
                        result.getString("email"),
                        result.getString("username"),
                        result.getString("password")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean updateUsername(int userID, String username)
    {
        String query = "UPDATE users SET username = ? WHERE userID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, username);
            statement.setInt(2, userID);

            statement.executeUpdate();

            if(statement.executeUpdate()>0)
            {
                return true;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateEmail(int userID, String email)
    {
        String query = "UPDATE users SET email = ? WHERE userID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, email);
            statement.setInt(2, userID);

            statement.executeUpdate();

            if(statement.executeUpdate()>0)
            {
                return true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePassword(int userID, String password)
    {
        String query = "UPDATE users SET password = ? WHERE userID = ?";

        try (PreparedStatement statement = connection.prepareStatement(query))
        {
            String hashedPassword = PasswordUtils.hashPassword(password);
            statement.setString(1, password);
            statement.setInt(2, userID);

            statement.executeUpdate();

            if(statement.executeUpdate()>0)
            {
                return true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}