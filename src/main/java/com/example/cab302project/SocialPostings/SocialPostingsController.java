package com.example.cab302project;

import com.example.cab302project.SocialPostings.ISocialPostingsDAO;
import com.example.cab302project.SocialPostings.SocialPostings;
import com.example.cab302project.SocialPostings.CreatePostController;
import com.example.cab302project.SocialPostings.PostDetailController;

import com.example.cab302project.Database.SocialPostingsDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller for the Social Postings page
 * Loads post from the database and build a 'card' in the UI for each one
 */
public class SocialPostingsController {

    /** Container that posts are added to */
    @FXML
    private VBox postsContainer;

    private final SocialPostingsDAO socialPostingsDAO = new SocialPostingsDAO();

    /**
     * Called automatically once the FXML file has finiished loading and this
     * triggers the load of the posts onto the user's feed
     */
    @FXML
    private void initialize() {
        loadPosts();
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

        // clicking anywhere opens the post detail page
        card.setOnMouseClicked(event -> openPostDetail(post));
        card.setStyle("-fx-cursor: hand;");

        registerButton.setOnMouseClicked(javafx.event.Event::consume);
        interestedButton.setOnMouseClicked(javafx.event.Event::consume);

        return card;
    }

    private void openPostDetail(SocialPostings post) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cab302project/postdetail.fxml")
            );
            Parent detailView = loader.load();

            StackPane mainContent = (StackPane) postsContainer.getScene().lookup("#mainContent");

            PostDetailController controller = loader.getController();
            controller.setMainContent(mainContent);
            controller.setPost(post);

            mainContent.getChildren().setAll(detailView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();

        try {
            List<SocialPostings> posts = socialPostingsDAO.getAllPosts();

            // sample posts for the community feed
            List<SocialPostings> mockPosts = List.of(
                    new SocialPostings(
                            -1, 1,
                            "Science Trivia Night",
                            "Come test your knowledge about everything science!",
                            "Either come alone or with a team to challenge other science students and go head to head!",
                            null,
                            "20/09/2026",
                            "16:00",
                            "20:00",
                            "QUT Gardens Point, V Block, Level 3"
                    ),

                    new SocialPostings(
                            -2, 2,
                            "Robotics Design Challenge",
                            "Come along to build robots!",
                            "Learn how to design, code, and build robots.",
                            null,
                            "25/09/2026",
                            "12:00",
                            "17:00",
                            "QUT Gardens Point, P Block, Room 413A"
                    ),

                    new SocialPostings(
                            -3, 3,
                            "Beach Walk",
                            "Come along for a casual and chill beach walk",
                            "Join the Social Brisbane Club for a chill walk along the beach to take a break from university!",
                            null,
                            "01/10/2026",
                            "07:00",
                            "10:00",
                            "Redcliffe Jetty"
                    )
            );

            List<SocialPostings> allPosts = new ArrayList<>(mockPosts);
            allPosts.addAll(posts);

            if (allPosts.isEmpty()) {
                Label emptyLabel = new Label("No posts on the community feed to display.");
                emptyLabel.getStyleClass().add("post-desc");
                postsContainer.getChildren().add(emptyLabel);
                return;
            }

            for (SocialPostings post : allPosts) {
                postsContainer.getChildren().add(buildPostCard(post));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Couldn't load posts right now.");
            errorLabel.getStyleClass().add("post-desc");
            postsContainer.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void onCreatePostClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/cab302project/create-post.fxml")
            );
            Parent createPostView = loader.load();

            StackPane mainContent = (StackPane) postsContainer.getScene().lookup("#mainContent");

            CreatePostController controller = loader.getController();
            controller.setMainContent(mainContent);

            mainContent.getChildren().setAll(createPostView);
        } catch (IOException e) {
            e.printStackTrace();
        }
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