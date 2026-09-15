package Authentication;

import Database.DatabaseUserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private final IUserDAO userDAO = new DatabaseUserDAO();

    @FXML
    private void handleRegister() {

        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty()
                || username.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            messageLabel.setText("Please complete all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        if (userDAO.emailExists(email)) {
            messageLabel.setText("That email is already registered.");
            return;
        }

        if (userDAO.usernameExists(username)) {
            messageLabel.setText("That username is already taken.");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        User user = new User(email, username, hashedPassword);

        boolean success = userDAO.registerUser(user);

        if (success) {
            messageLabel.setText("Account created successfully.");

            emailField.clear();
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

        } else {
            messageLabel.setText("Unable to create account.");
        }
    }

    @FXML
    private void goToLogin() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/App/login.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        Stage stage =
                (Stage) emailField
                        .getScene()
                        .getWindow();

        stage.setScene(scene);
    }
}