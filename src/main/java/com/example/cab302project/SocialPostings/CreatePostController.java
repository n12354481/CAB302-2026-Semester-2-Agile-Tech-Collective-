package com.example.cab302project.SocialPostings;

import com.example.cab302project.Database.SocialPostingsDAO;

import com.example.cab302project.HelloApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import java.util.List;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.sql.SQLException;
import java.util.ArrayList;

public class CreatePostController {
    
    @FXML private TextField eventNameField;
    @FXML private TextArea eventDescriptionField;
    @FXML private TextField eventDateField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField eventLocationField;
    @FXML private FlowPane tagsContainer;
    @FXML private Label statusLabel;

    private final ISocialPostingsDAO socialPostingsDAO = new SocialPostingsDAO();

    // replace with actual logged in user ID
    private int currentUserId = 1;

    public void setUserId(int userId) {
        this.currentUserId = userId;
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private StackPane mainContent;

    /**
     * Called by when the screen is opened so the page could navigate
     * somewhere else later if needed
     * @param mainContent
     */
    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    private static final String TAG_UNSELECTED_STYLE =
            "-fx-background-color: #E0E0E0; -fx-background-radius: 12; -fx-padding: 4 10;";
    private static final String TAG_SELECTED_STYLE =
            "-fx-background-color: #1F6F64; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10;";

    @FXML
    private void initialize() {
        for (String tag : PostTags.AVAILABLE_TAGS) {
            ToggleButton tagButton = new ToggleButton(tag);
            tagButton.setStyle(TAG_UNSELECTED_STYLE);
            tagButton.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                tagButton.setStyle(isSelected ? TAG_SELECTED_STYLE : TAG_UNSELECTED_STYLE);
            });
            tagsContainer.getChildren().add(tagButton);
        }
    }

    private String getSelectedTags() {
        List<String> selected = new ArrayList<>();
        for (javafx.scene.Node node : tagsContainer.getChildren()) {
            if (node instanceof ToggleButton toggleButton && toggleButton.isSelected()) {
                selected.add(toggleButton.getText());
            }
        }
        return String.join(",", selected);
    }

    @FXML
    private void onPostEventClicked() {
        statusLabel.setText("");

        String title = eventNameField.getText();
        String description = eventDescriptionField.getText();
        String location = eventLocationField.getText();
        String dateText = eventDateField.getText();
        String startTimeText = startTimeField.getText();
        String endTimeText = endTimeField.getText();
        String tags = getSelectedTags();

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
                currentUserId,
                title,
                description,
                description,
                null,
                dateText,
                startTimeText,
                endTimeText,
                location,
                getSelectedTags()
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
        for (javafx.scene.Node node : tagsContainer.getChildren()) {
            if (node instanceof ToggleButton toggleButton) {
                toggleButton.setSelected(false);
            }
        }
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
