package com.example.cab302project.SocialPostings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

import com.example.cab302project.Database.SocialPostingsDAO;

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

    @FXML private Region postImage;

    private StackPane mainContent;

    private int currentUserId;

    @FXML private Button registerButton;
    @FXML private Button interestedButton;

    private final PostEngagement engagement = new PostEngagement(new SocialPostingsDAO());
    private SocialPostings currentPost;

    public void setUserId(int userId) {
        this.currentUserId = userId;
    }

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
        this.currentPost = post;
        titleLabel.setText(post.title());
        descriptionLabel.setText(post.description());
        contentLabel.setText(post.content());
        locationLabel.setText(post.eventLocation());

        PostImages.showImage(postImage, post.image());

        String time = "";
        if (post.startTime() != null && !post.startTime().isBlank()) {
            time += post.startTime();
        }
        if (post.endTime() != null && !post.endTime().isBlank()) {
            time += " - " + post.endTime();
        }
        timeLabel.setText(time);

        setRegisterButtonLabel(engagement.isRegistered(currentUserId, post));
        setInterestedButtonLabel(engagement.isInterested(currentUserId, post));
    }

    @FXML
    private void onBackClicked() throws IOException{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cab302project/socialpostings.fxml")
            );
            Node content = loader.load();

            SocialPostingsController controller = loader.getController();
            controller.setUserId(currentUserId);

            mainContent.getChildren().setAll(content);
        }

    @FXML
    private void onRegisterClicked() {
        setRegisterButtonLabel(engagement.toggleRegistration(currentUserId, currentPost));
    }

    @FXML
    private void onInterestedClicked() {
        setInterestedButtonLabel(engagement.toggleInterest(currentUserId, currentPost));
    }

    private void setRegisterButtonLabel(boolean registered) {
        registerButton.getStyleClass().remove("register-btn-active");
        if (registered) {
            registerButton.setText("Registered ✓");
            registerButton.getStyleClass().add("register-btn-active");
        } else {
            registerButton.setText("Register Here!");
        }
    }

    private void setInterestedButtonLabel(boolean interested) {
        interestedButton.getStyleClass().remove("interested-btn-active");
        if (interested) {
            interestedButton.setText("Interested ✓");
            interestedButton.getStyleClass().add("interested-btn-active");
        } else {
            interestedButton.setText("Interested!");
        }
    }

    @FXML
    private void onContactInfoClicked() {

    }
}
