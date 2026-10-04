package com.example.cab302project.SocialPostings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controller for the full post details, which shows the user more
 * information if they're interested
 */
public class PostDetailController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label contentLabel;
    @FXML private Label locationLabel;
    @FXML private Label timeLabel;

    private StackPane mainContent;

    /**
     * Called by when the screen is opened
     * @param mainContent
     */
    public void setMainContent(StackPane mainContent) {
        this.mainContent = mainContent;
    }

    /**
     * Called to populate the fields with the post's data and information
     * @param post
     */
    public void setPost(SocialPostings post) {
        titleLabel.setText(post.title());
        descriptionLabel.setText(post.description());
        contentLabel.setText(post.content());
        locationLabel.setText(post.eventLocation());

        String time = "";
        if (post.startTime() != null && !post.startTime().isBlank()) {
            time += post.startTime();
        }
        if (post.endTime() != null && !post.endTime().isBlank()) {
            time += " - " + post.endTime();
        }
        timeLabel.setText(time);
    }

    @FXML
    private void onBackClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cab302project/socialpostings.fxml")
            );
            Parent feedView = loader.load();
            mainContent.getChildren().setAll(feedView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onContactInfoClicked() {

    }
}
