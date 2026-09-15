package Authentication;

import Database.DatabaseUserDAO;
import App.HelloController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final IUserDAO userDAO = new DatabaseUserDAO();

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Check that both fields have been completed
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter your username and password.");
            return;
        }

        // Search the database for the username/password combination
        User user = userDAO.loginUser(username, password);

        if (user != null) {
            messageLabel.setText("Login successful!");

            System.out.println(
                    "Logged in as: " + user.getUsername()
            );

            try {
                goToHomePage(user);
            } catch (IOException e) {
                messageLabel.setText("Unable to open the home page.");
                e.printStackTrace();
            }

        } else {
            messageLabel.setText("Incorrect username or password.");
        }
    }

    @FXML
    private void goToRegister() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/App/registration.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        Stage stage = (Stage)
                usernameField.getScene().getWindow();

        stage.setScene(scene);
    }

    private void goToHomePage(User user) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/App/hello-view.fxml"
                )
        );

        Scene scene = new Scene(loader.load());

        HelloController controller = loader.getController();
        controller.setUserID(user.getUserID());

        Stage stage =
                (Stage) usernameField
                        .getScene()
                        .getWindow();

        stage.setScene(scene);
    }
}