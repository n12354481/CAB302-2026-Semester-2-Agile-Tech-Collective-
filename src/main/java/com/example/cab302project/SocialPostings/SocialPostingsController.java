package com.example.cab302project.SocialPostings;

import com.example.cab302project.SocialPostings.ISocialPostingsDAO;
import com.example.cab302project.SocialPostings.SocialPostings;
import com.example.cab302project.SocialPostings.CreatePostController;
import com.example.cab302project.SocialPostings.PostDetailController;
import com.example.cab302project.SocialPostings.PostTags;

import com.example.cab302project.Database.SocialPostingsDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.Parent;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;


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

    private final PostEngagement engagement = new PostEngagement(socialPostingsDAO);

    @FXML
    private VBox interestedEventsList;

    /**
     * Called automatically once the FXML file has finiished loading and this
     * triggers the load of the posts onto the user's feed
     */
    @FXML
    private void initialize() {
        loadPosts();
        refreshUpcomingEvents();
        refreshInterestedEvents();
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        refreshUpcomingEvents();
        refreshInterestedEvents();
    }

    private void refreshUpcomingEvents() {
        List<SocialPostings> registered = new ArrayList<>();

        try {
            registered.addAll(socialPostingsDAO.getRegisteredPosts(currentUserId));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Set<Integer> registeredMockPosts = engagement.getRegisteredMockIds(currentUserId);

        for (SocialPostings post : allPosts) {
            if (post.postId() < 0 && registeredMockPosts.contains(post.postId())) {
                registered.add(post);
            }
        }

        fillEventList(upcomingEventsList, registered, "You haven't registered for any events!", "Unregister", post -> {
            engagement.toggleRegistration(currentUserId, post);
            refreshUpcomingEvents();
            applyFilters();
        });
    }

    private void refreshInterestedEvents() {
        List<SocialPostings> interested = new ArrayList<>();

        try {
            interested.addAll(socialPostingsDAO.getInterestedPosts(currentUserId));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Set<Integer> mockInterests = engagement.getInterestedMockIds(currentUserId);

        for (SocialPostings post : allPosts) {
            if (post.postId() < 0 && mockInterests.contains(post.postId())) {
                interested.add(post);
            }
        }

        fillEventList(interestedEventsList, interested, "You haven't marked any events as interested!", "Remove from interested", post -> {
            engagement.toggleInterest(currentUserId, post);
            refreshInterestedEvents();
            applyFilters();
        });
    }

    private void fillEventList(VBox list, List<SocialPostings> posts, String emptyText, String removeTip, java.util.function.Consumer<SocialPostings> onRemove) {
        list.getChildren().clear();

        if (posts.isEmpty()) {
            Label emptyLabel = new Label(emptyText);
            emptyLabel.setWrapText(true);
            emptyLabel.getStyleClass().add("event-empty");
            list.getChildren().add(emptyLabel);
            return;
        }

        for (SocialPostings post : posts) {
            Label eventTitle = new Label(post.title());
            eventTitle.setWrapText(true);
            eventTitle.getStyleClass().add("event-item-title");

            String when = post.eventDate() == null ? "" : post.eventDate();
            if (post.startTime() != null && !post.startTime().isBlank()) {
                when += " • " + post.startTime();
            }
            Label eventWhen = new Label(when);
            eventWhen.getStyleClass().add("event-item-date");

            VBox text = new VBox(2, eventTitle, eventWhen);
            HBox.setHgrow(text, Priority.ALWAYS);

            Button removeButton = new Button("X");
            removeButton.getStyleClass().add("remove-event-btn");
            removeButton.setTooltip(new Tooltip(removeTip));
            removeButton.setOnAction(e -> onRemove.accept(post));
            removeButton.setOnMouseClicked(javafx.event.Event::consume);

            HBox row = new HBox(6, text, removeButton);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("event-item");
            row.setOnMouseClicked(e -> openPostDetail(post));
            list.getChildren().add(row);
        }
    }

    private VBox buildPostCard(SocialPostings post) {
        VBox card = new VBox();
        card.getStyleClass().add("post-card");

        Region image = new Region();
        image.getStyleClass().add("post-image");

        image.getStyleClass().add("post-image-side");
        image.setPrefSize(220, 150);
        image.setMinSize(220, 150);
        PostImages.showImage(image, post.image());

        VBox body = new VBox(6);
        body.setPadding(new Insets(10, 14, 14, 14));
        HBox.setHgrow(body, Priority.ALWAYS);

        Label title = new Label(post.title());
        title.getStyleClass().add("post-title");

        Label description = new Label(post.description());
        description.setWrapText(true);
        description.getStyleClass().add("post-desc");

        HBox buttons = new HBox(8);
        Button registerButton = new Button();
        registerButton.getStyleClass().add("register-btn");

        setRegisterButtonLabel(registerButton, engagement.isRegistered(currentUserId, post));

        Button interestedButton = new Button("Interested!");
        interestedButton.getStyleClass().add("interested-btn");
        setInterestedButtonLabel(interestedButton, engagement.isInterested(currentUserId, post));
        interestedButton.setOnAction(e -> {
            boolean interested = engagement.toggleInterest(currentUserId, post);
            setInterestedButtonLabel(interestedButton, interested);
            refreshInterestedEvents();
        });

        registerButton.setOnAction(e -> {
            boolean registered = engagement.toggleRegistration(currentUserId, post);
            setRegisterButtonLabel(registerButton, registered);

            setInterestedButtonLabel(interestedButton, engagement.isInterested(currentUserId, post));
            refreshUpcomingEvents();
            refreshInterestedEvents();
        });

        buttons.getChildren().addAll(registerButton, interestedButton);

        body.getChildren().addAll(title, description, buttons);
        card.getChildren().add(new HBox(image, body));

        // clicking anywhere opens the post detail page
        card.setOnMouseClicked(event -> openPostDetail(post));
        card.setStyle("-fx-cursor: hand;");

        registerButton.setOnMouseClicked(javafx.event.Event::consume);
        interestedButton.setOnMouseClicked(javafx.event.Event::consume);

        return card;
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

    private void setInterestedButtonLabel(Button interestedButton, boolean interested) {
        if (interested) {
            interestedButton.setText("Interested ✓");
            interestedButton.getStyleClass().add("interested-btn-active");
        } else {
            interestedButton.setText("Interested!");
            interestedButton.getStyleClass().remove("interested-btn-active");
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
                            "/com/example/cab302project/social-images/science-trivia.jpg",
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
                            "/com/example/cab302project/social-images/robotics-design.jpg",
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
                            "/com/example/cab302project/social-images/beach-walk.jpg",
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
                        "/com/example/cab302project/social-images/astronomy-night.jpg",
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
                            "/com/example/cab302project/social-images/hackathon.jpg",
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
                            "/com/example/cab302project/social-images/garden-picnic.jpg",
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
                            "/com/example/cab302project/social-images/machine-workshop.jpg",
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
                            "/com/example/cab302project/social-images/markets-meetup.jpg",
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
                            "/com/example/cab302project/social-images/chemistry-show.jpg",
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