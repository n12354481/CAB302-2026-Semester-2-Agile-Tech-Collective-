package App;

import SocialPostings.ISocialPostingsDAO;
import SocialPostings.SocialPostings;

import SocialPostings.SocialPostingsDAO;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.sql.SQLException;
import java.util.List;


/**
 * Controller for the Social Postings page
 * Loads post from the database and build a 'card' in the UI for each one
 */
public class SocialPostingsController {

    /** Container that posts are added to */
    @FXML
    private VBox postsContainer;

    private final ISocialPostingsDAO socialPostingsDAO = new SocialPostings.SocialPostingsDAO();

    /**
     * Called automatically once the FXML file has finiished loading and this
     * triggers the load of the posts onto the user's feed
     */
    @FXML
    private void initialize() {
        loadPosts();
    }

    /**
     * Pulls all the posts from the database and rebuilds the feed
     * safe to call again and again to refresh what's displayed e.g applying filters
     */
    private void loadPosts() {
        postsContainer.getChildren().clear();

        try {
            List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

            if (posts.isEmpty()) {
                Label emptyLabel = new Label("No posts on the community feed to display.");
                emptyLabel.getStyleClass().add("post-desc");
                postsContainer.getChildren().add(emptyLabel);
                return;
            }

            for (SocialPostings post : posts) {
                postsContainer.getChildren().add(buildPostCard(post));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Couldn't load posts right now.");
            errorLabel.getStyleClass().add("post-desc");
            postsContainer.getChildren().add(errorLabel);
        }
    }


    private VBox buildPostCard(SocialPostings post) {
        VBox card = new VBox();
        card.getStyleClass().add("post-card");

    Region image = new Region();
        image.getStyleClass().add("post-image");
        image.setPrefHeight(160);

        VBox body = new VBox(6);
        body.setPadding(new Insets(10, 14, 14, 14));

        Label title = new Label(post.title());
        title.getStyleClass().add("post-title");

        Label description = new Label(post.description());
        description.setWrapText(true);
        description.getStyleClass().add("post-desc");

        HBox buttons = new HBox(8);
        Button registerButton = new Button("Register Here!");
        registerButton.getStyleClass().add("register-btn");
        Button interestedButton = new Button("Interested!");
        interestedButton.getStyleClass().add("interested-btn");
        buttons.getChildren().addAll(registerButton, interestedButton);

        body.getChildren().addAll(title, description, buttons);
        card.getChildren().addAll(image, body);

        return card;
    }

    @FXML
    private void onCreatePostClicked() {
        // create a post button
    }

    @FXML
    private  void onClearFiltersClicked() {
        // when users clear filters
    }

    @FXML
    private void onFiltersClicked() {
        // when users want to add filters to search
    }
}