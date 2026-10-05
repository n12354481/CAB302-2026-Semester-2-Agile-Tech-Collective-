package com.example.cab302project.SocialPostings;

import com.example.cab302project.SocialPostings.ISocialPostingsDAO;
import com.example.cab302project.SocialPostings.SocialPostings;
import com.example.cab302project.SocialPostings.CreatePostController;
import com.example.cab302project.SocialPostings.PostDetailController;
import com.example.cab302project.SocialPostings.PostTags;

import com.example.cab302project.Database.SocialPostingsDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;


/**
 * Controller for the Social Postings page
 * Loads post from the database and build a 'card' in the UI for each one
 */
public class SocialPostingsController {

    /** Container that posts are added to */
    @FXML
    private VBox postsContainer;

    private final SocialPostingsDAO socialPostingsDAO = new SocialPostingsDAO();

    @FXML
    private HBox activeFiltersContainer;

    @FXML
    private HBox filterOptionsRow;

    @FXML
    private VBox upcomingEventsList;

    private List<SocialPostings> allPosts = new ArrayList<>();

    private final Set<String> selectedFilters = new LinkedHashSet<>();

    private boolean filterOptionsBuilt = false;

    private int currentUserId;

    private final Map<Integer, Set<Integer>> registeredMockPostIds = new HashMap<>();

    /**
     * Called automatically once the FXML file has finiished loading and this
     * triggers the load of the posts onto the user's feed
     */
    @FXML
    private void initialize() {
        loadPosts();
        refreshUpcomingEvents();
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        refreshUpcomingEvents();
    }

    private void refreshUpcomingEvents() {
        List<SocialPostings> registered = new ArrayList<>();

        try {
            registered.addAll(socialPostingsDAO.getRegisteredPosts(currentUserId));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Add registered mock posts
        Set<Integer> registeredMockPosts =
                registeredMockPostIds.getOrDefault(currentUserId, new HashSet<>());

        for (SocialPostings post : allPosts) {
            if (post.postId() < 0 && registeredMockPosts.contains(post.postId())) {
                registered.add(post);
            }
        }

        upcomingEventsList.getChildren().clear();

        if (registered.isEmpty()) {
            Label emptyLabel = new Label("You haven't registered for any events!");
            emptyLabel.setWrapText(true);
            emptyLabel.getStyleClass().add("upcoming-event-item");
            upcomingEventsList.getChildren().add(emptyLabel);
            return;
        }

        for (SocialPostings post : registered) {
            Label eventLabel = new Label(
                    post.title() + " • " + post.eventDate()
            );
            eventLabel.setWrapText(true);
            eventLabel.getStyleClass().add("upcoming-event-item");
            upcomingEventsList.getChildren().add(eventLabel);
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
        Button registerButton = new Button();
        registerButton.getStyleClass().add("register-btn");

        setRegisterButtonLabel(registerButton, isRegistered(post));

        registerButton.setOnAction(e ->
                toggleRegistration(post, registerButton)
        );

        Button interestedButton = new Button("Interested!");

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

    private boolean isRegistered(SocialPostings post) {
        if (post.postId() < 0) {
            Set<Integer> registeredMockPosts =
                    registeredMockPostIds.getOrDefault(currentUserId, new HashSet<>());

            return registeredMockPosts.contains(post.postId());
        }

        try {
            return socialPostingsDAO.isRegistered(
                    currentUserId,
                    post.postId()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void toggleRegistration(
            SocialPostings post,
            Button registerButton) {

        boolean currentlyRegistered = isRegistered(post);

        try {
            if (post.postId() < 0) {

                Set<Integer> registeredMockPosts =
                        registeredMockPostIds.computeIfAbsent(
                                currentUserId,
                                id -> new HashSet<>()
                        );

                if (currentlyRegistered) {
                    registeredMockPosts.remove(post.postId());
                } else {
                    registeredMockPosts.add(post.postId());
                }

            } else {

                if (currentlyRegistered) {
                    socialPostingsDAO.unregisterFromPost(
                            currentUserId,
                            post.postId()
                    );
                } else {
                    socialPostingsDAO.registerForPost(
                            currentUserId,
                            post.postId()
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        setRegisterButtonLabel(registerButton, !currentlyRegistered);
        refreshUpcomingEvents();
    }

    private void setRegisterButtonLabel(
            Button registerButton,
            boolean registered) {

        if (registered) {
            registerButton.setText("Registered ✓");
            registerButton.getStyleClass().add("register-btn-active");
        } else {
            registerButton.setText("Register Here!");
            registerButton.getStyleClass().remove("register-btn-active");
        }
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
            controller.setUserId(currentUserId);
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
                            -1, 0,
                            "Science Trivia Night",
                            "Come test your knowledge about everything science!",
                            "Either come alone or with a team to challenge other science students and go head to head!",
                            null,
                            "20/09/2026",
                            "16:00",
                            "20:00",
                            "QUT Gardens Point, V Block, Level 3",
                            "Science, Trivia"
                    ),

                    new SocialPostings(
                            -2, 0,
                            "Robotics Design Challenge",
                            "Come along to build robots!",
                            "Learn how to design, code, and build robots.",
                            null,
                            "25/09/2026",
                            "12:00",
                            "17:00",
                            "University of Queensland, St Lucia, Block A, Room A123",
                            "Robotics, UQ"
                    ),

                    new SocialPostings(
                            -3, 0,
                            "Beach Walk",
                            "Come along for a casual and chill beach walk",
                            "Join the Social Brisbane Club for a chill walk along the beach to take a break from university!",
                            null,
                            "01/10/2026",
                            "07:00",
                            "10:00",
                            "Redcliffe Jetty",
                            "Social"
                    ),

                new SocialPostings(
                    -4, 0,
                    "Astronomy Night",
                    "Stargazing and telescope viewing for all experience levels",
                    "Bring a blanket and come look through our telescopes! We'll have a short talk on the current night sky before we get started.",
                    null,
                    "08/10/2026",
                    "19:00",
                    "21:30",
                    "QUT Gardens Point, Gardens Theatre Lawn",
                    "Science, Social"
            ),

                    new SocialPostings(
                            -5, 0,
                            "Coding Club Hackday",
                            "A relaxed one-day hackathon for all skill levels",
                            "Form a team or work solo on a small project of your choice. Mentors will be on hand, and there's pizza at lunch!",
                            null,
                            "14/10/2026",
                            "10:00",
                            "16:00",
                            "QUT Gardens Point, P Block, Room 502",
                            "Competition, QUT, Social"
                    ),

                    new SocialPostings(
                            -6, 0,
                            "Botanic Gardens Picnic",
                            "Casual picnic meetup, bring your own food and a mat",
                            "A laid-back afternoon to meet other students over snacks in the gardens. Open to everyone, no need to RSVP.",
                            null,
                            "18/10/2026",
                            "12:00",
                            "14:00",
                            "Brisbane City Botanic Gardens",
                            "Brisbane, Social"
                    ),

                    new SocialPostings(
                            -7, 0,
                            "Intro to Machine Learning Workshop",
                            "A beginner-friendly hands-on workshop on ML basics",
                            "We'll cover the basics of machine learning and get a simple model running together. Laptops provided if you don't have one.",
                            null,
                            "22/10/2026",
                            "14:00",
                            "16:30",
                            "QUT Gardens Point, P Block, Room 404",
                            "STEM, Workshop, Robotics"
                    ),

                    new SocialPostings(
                            -8, 0,
                            "South Bank Markets Meetup",
                            "Explore the Sunday markets as a group",
                            "Meet at the entrance and we'll wander the stalls together, grab some food, and enjoy the river views.",
                            null,
                            "25/10/2026",
                            "09:00",
                            "11:00",
                            "South Bank Parklands",
                            "Brisbane, Social"
                    ),

                    new SocialPostings(
                            -9, 0,
                            "Chemistry Demo Show",
                            "Live chemistry demonstrations and experiments",
                            "Come watch some fun chemistry demonstrations, with a Q&A with the presenters afterwards.",
                            null,
                            "29/10/2026",
                            "15:00",
                            "16:00",
                            "QUT Gardens Point, S Block, Lecture Theatre 2",
                            "Science, STEM"
                    )
            );

            allPosts = new ArrayList<>(mockPosts);
            allPosts.addAll(posts);

            applyFilters();

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Couldn't load posts right now.");
            errorLabel.getStyleClass().add("post-desc");
            postsContainer.getChildren().add(errorLabel);
        }
    }

    private void applyFilters() {
        List<SocialPostings> filtered;

        if (selectedFilters.isEmpty()) {
            filtered = allPosts;
        } else {
            filtered = allPosts.stream().filter(post -> postHasAllTags(post, selectedFilters)).toList();
        }

        renderPosts(filtered);
    }

    private boolean postHasAllTags(SocialPostings post, Set<String> requiredTags) {
        if (post.tags() == null || post.tags().isBlank()) {
            return false;
        }

        Set<String> postTags = new LinkedHashSet<>();

        for (String tag : post.tags().split(",")) {
            postTags.add(tag.trim());
        }

        return postTags.containsAll(requiredTags);
    }

    private void renderPosts(List<SocialPostings> posts) {
        postsContainer.getChildren().clear();

        if (posts.isEmpty()) {
            Label emptyLabel = new Label("No posts match your filters.");
            emptyLabel.getStyleClass().add("post-desc");
            postsContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (SocialPostings post : posts) {
            postsContainer.getChildren().add(buildPostCard(post));
        }
    }

    private void buildFilterOptions() {
        filterOptionsRow.getChildren().clear();

        for (String tag : PostTags.AVAILABLE_TAGS) {
            ToggleButton tagButton = new ToggleButton(tag);

            tagButton.setStyle(filterButtonStyle(false));
            tagButton.setSelected(selectedFilters.contains(tag));

            tagButton.setOnAction(e -> {
                if (tagButton.isSelected()) {
                    selectedFilters.add(tag);
                } else {
                    selectedFilters.remove(tag);
                }

                tagButton.setStyle(filterButtonStyle(tagButton.isSelected()));

                refreshActiveFilterPills();
                applyFilters();
            });

            filterOptionsRow.getChildren().add(tagButton);
        }
    }

    private void refreshFilterOptionButtons() {
        for (var node : filterOptionsRow.getChildren()) {
            if (node instanceof ToggleButton toggleButton) {
                toggleButton.setSelected(selectedFilters.contains(toggleButton.getText()));

                toggleButton.setStyle(filterButtonStyle(toggleButton.isSelected()));
            }
        }
    }

    private String filterButtonStyle(boolean selected) {
        return selected
                ? "-fx-background-color: #1F6F64; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 4 10;"
                : "-fx-background-color: #e0e0e0; -fx-background-radius: 12; -fx-padding: 4 10;";
    }

    private void refreshActiveFilterPills() {
        activeFiltersContainer.getChildren().clear();

        for (String tag : selectedFilters) {
            Button pill = new Button("X " + tag.toUpperCase());

            pill.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: #2b8570; " +
                    "-fx-border-radius: 12; " +
                    "-fx-padding: 4 10; " +
                    "-fx-font-size: 11px;");

            pill.setOnAction(e -> {
                selectedFilters.remove(tag);
                refreshActiveFilterPills();
                refreshFilterOptionButtons();
                applyFilters();
            });

            activeFiltersContainer.getChildren().add(pill);
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
            controller.setUserId(currentUserId);

            mainContent.getChildren().setAll(createPostView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private  void onClearFiltersClicked() {
        selectedFilters.clear();
        refreshActiveFilterPills();
        refreshFilterOptionButtons();
        applyFilters();
    }

    @FXML
    private void onFiltersClicked() {
        if (!filterOptionsBuilt) {
            buildFilterOptions();
            filterOptionsBuilt = true;
        }

        boolean nowVisible = !filterOptionsRow.isVisible();

        filterOptionsRow.setVisible(nowVisible);
        filterOptionsRow.setManaged(nowVisible);
    }
}