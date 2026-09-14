package com.example.cab302project.SocialPostings;

import com.example.cab302project.Database.SocialPostingsDAO;

import com.example.cab302project.HelloApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
// import javafx.scene.Parent;
import javafx.scene.Scene;
// import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.sql.SQLException;

public class CreatePostController {
    
    @FXML private TextField eventNameField;
    @FXML private TextArea eventDescriptionField;
    @FXML private TextField eventDateField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField eventLocationField;
    @FXML private Label statusLabel;

    private StackPane mainContent;

    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    private final ISocialPostingsDAO socialPostingsDAO = new SocialPostingsDAO();

    // replace with actual logged in user ID
    private static final int CURRENT_USER_ID = 1;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void onPostEventClicked() {
        statusLabel.setText("");

        String title = eventNameField.getText();
        String description = eventDescriptionField.getText();
        String location = eventLocationField.getText();
        String dateText = eventDateField.getText();
        String startTimeText = startTimeField.getText();
        String endTimeText = endTimeField.getText();

        if (title == null || title.isBlank()) {
            statusLabel.setStyle("-fx-text-fill: #C0392B;");
            statusLabel.setText("Event name is required.");
            return;
        }

        if (dateText != null && !dateText.isBlank() && !isValidDate(dateText)) {
            statusLabel.setStyle("-fx-text-fill: #C0392B;");
            statusLabel.setText("Event date must be in DD/MM/YYYY format.");
            return;
        }

        if (startTimeText != null && !startTimeText.isBlank() && !isValidTime(startTimeText)) {
            statusLabel.setStyle("-fx-text-fill: #C0392B;");
            statusLabel.setText("Start time must be in HH:mm format.");
            return;
        }

        if (endTimeText != null && !endTimeText.isBlank() && !isValidTime(endTimeText)) {
            statusLabel.setStyle("-fx-text-fill: #C0392B;");
            statusLabel.setText("End time must be in HH:mm format.");
            return;
        }

        SocialPostings post = new SocialPostings(
                0,
                CURRENT_USER_ID,
                title,
                description,
                description,
                null,
                dateText,
                startTimeText,
                endTimeText,
                location
        );

        try {
            int newPostId = socialPostingsDAO.createPost(post);

            if (newPostId != -1) {
                statusLabel.setStyle("-fx-text-fill: #1F6F5C;");
                statusLabel.setText("Post created successfully!");
                clearForm();
            } else {
                statusLabel.setStyle("-fx-text-fill: #C0392B");
                statusLabel.setText("Something went wrong, please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setStyle("-fx-text-fill: #C0392B;");
            statusLabel.setText("Database error, could not save the post.");
        }
    }

    @FXML
    private void onSaveDraftClicked() {
        // will implement saving a post for drafts
        statusLabel.setText("Draft saving isn't avaliable yet.");
    }

    @FXML
    private void onBackClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("socialpostings.fxml")
        );

        Node content = loader.load();

        mainContent.getChildren().setAll(content);
    }

    private void clearForm() {
        eventNameField.clear();
        eventDescriptionField.clear();
        eventDateField.clear();
        startTimeField.clear();
        endTimeField.clear();
        eventLocationField.clear();
    }

    private boolean isValidDate(String text) {
        try {
            LocalDate.parse(text, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String text) {
        try {
            LocalTime.parse(text);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
