package com.example.cab302project.Activities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class ActivitiesController implements Initializable {
    private final List<Activity> activities = new ArrayList<>();
    private Activity selectedActivity;
    private Pane mainContent;
    private Node browsePage;

    @FXML
    private AnchorPane activityRoot;

    @FXML
    private ButtonBar buttonBar;

    @FXML
    private FlowPane contentPane;

    @FXML
    private Label activityName;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label goalLabel;

    @FXML
    private ImageView activityImage;

    @FXML
    private Label imagePlaceholder;

    @FXML
    private ToggleGroup durationGroup;

    @FXML
    private RadioButton customDuration;

    @FXML
    private TextField customMinutes;

    @FXML
    private Label selectionMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (contentPane != null) {
            loadActivities();

            List<String> categories = new ArrayList<>();
            for (Activity activity : activities) {
                if (!categories.contains(activity.getCategory())) {
                    categories.add(activity.getCategory());
                }
            }

            for (String category : categories) {
                Button button = new Button(category);
                button.setOnAction(event -> changeCategory(category));
                buttonBar.getButtons().add(button);
            }

            if (!categories.isEmpty()) {
                changeCategory(categories.get(0));
            }
        }

        if (customMinutes != null && customDuration != null) {
            customMinutes.visibleProperty().bind(customDuration.selectedProperty());
            customMinutes.managedProperty().bind((customDuration.selectedProperty()));
        }
    }
        private void loadActivities() {
            activities.add(new Activity(1, "Swimming", "Fitness",
                    "Swimming is a full body physical activity that can help improve fitness while also giving you a break from studying and sitting for long periods of time. It can be done at your own pace, whether you want to swim a few relaxed laps or have more active workout.",
                    30, "Swimming.jpg"));

            activities.add(new Activity(2, "Jogging", "Fitness",
                    "Jogging is a simple way to stay physically active and take some time away from studying or sitting at a desk. You can jog around campus, your neighbourhood or a nearby park at a pace that feels comfortable. It can also be a good way to clear your mind and have a break after spending a long time working on university tasks.",
                    30, null));

            activities.add(new Activity(3, "Tennis", "Fitness",
                    "Tennis is an active sport that can help improve fitness, coordination and concentration. It can be played casually or competitively with another person, making it a good way to exercise while also spending time with friends and taking a break from studying.",
                    30, "Tennis.png"));

            activities.add(new Activity(4, "Study with a friend", "Social",
                    "Studying with a friend gives you the chance to work through university content together, discuss difficult topics and help each other when something is confusing. It can make studying feel less isolating and can also help you stay motivated and focused on what you need to complete.",
                    30, null));

            activities.add(new Activity(5, "Attend a workshop", "Social",
                    "Attend a workshop to learn something new or develop skills outside of your normal classes. Workshops can give you practical experience, introduce you to different topics and provide an opportunity to meet other students who may have similar academic or career interests.",
                    30, null));

            activities.add(new Activity(6, "Join a STEM society event", "Social",
                    "Take part in an event organised by a STEM-related student society at university. These events can be a good way to meet students with similar interests, learn more about different areas of STEM and get involved with the university community outside of classes.",
                    30, null));

            activities.add(new Activity(7, "Meditation", "Others",
                    "Meditation is a simple activity where you take some time away from studying and other distractions to slow down and focus on the present moment. Even a short meditation session can give you some quiet time to relax, clear your mind and reset before continuing with your day.",
                    30, null));

            activities.add(new Activity(8, "Review lecture notes", "Others",
                    "Spend some time going back through notes from your recent lectures or tutorials to refresh your understanding of the content. Regularly reviewing notes can help you identify topics you are unsure about and avoid leaving all of your revision until right before an assessment or exam.",
                    30, null));

            activities.add(new Activity(9, "Coding Practice", "Others",
                    "Spend some time practising programming outside of your required classwork. You could work through coding exercises, practise concepts you found difficult in class or experiment with a small problem. Regular practice can help you become more comfortable with programming and problem solving over time.",
                    30, null));

            activities.add(new Activity(10, "Reading", "Others",
                    "Take some time to read something you enjoy outside of your usual university work. This could be a novel, short story, magazine or another topic that interests you. Reading can be a relaxing way to spend some time away from assignments, coding and screens.",
                    30, null));

            activities.add(new Activity(11, "Gym workout", "Fitness",
                    "Complete a gym workout based on your own fitness level and goals. This could include strength training, cardio or a combination of different exercises. Going to the gym can help you stay physically active, especially when a lot of your university work involves sitting at a desk or computer.",
                    30, null));

            activities.add(new Activity(12, "Go for a walk", "Others",
                    "Take a break from your desk and go for a walk around campus, your neighbourhood or somewhere outdoors. Walking is a simple way to get some movement into your day and can give you a chance to clear your head after spending a long time studying or working on an assignment.",
                    30, null));

            activities.add(new Activity(13, "University club event", "Social",
                    "Attend an event organised by one of the university's student clubs or societies. It is an opportunity to take a break from academic work, try something different and meet other students who share similar interests. It can also help you feel more involved in university life outside of classes.",
                    30, null));

            activities.add(new Activity(14, "Lunch with a friend", "Social",
                    "Take some time away from studying to have lunch with a friend or classmate. It gives you a chance to catch up, talk about things outside of university work and have a proper break during a busy day instead of spending the whole day studying by yourself.",
                    30, null));
        }

        private void changeCategory(String category) {
            contentPane.getChildren().clear();

            for (Node node : buttonBar.getButtons()) {
                Button button = (Button) node;
                button.setStyle(button.getText().equals(category) ? "-fx-background-color: #386F65; -fx-text-fill: white;" : "-fx-background-color: #E8EEEE;");
            }

            for (Activity activity : filterByCategory(category)) {
                Button button = new Button(activity.getName());
                button.setPrefSize(230, 260);
                button.setWrapText(true);
                button.setContentDisplay(ContentDisplay.TOP);
                button.setGraphicTextGap(10);
                button.setTooltip(new Tooltip(activity.getDescription()));

                button.setStyle("-fx-background-color: white; -fx-border-color: #AAAAAA;" + "-fx-border-radius:15; -fx-background-radius: 15; -fx-cursor: hand;");
                Image image = loadImage(activity);

                if (image != null) {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(185);
                    imageView.setFitWidth(205);
                    imageView.setPreserveRatio(true);
                    button.setGraphic(imageView);
                } else {
                    StackPane placeholder = new StackPane(new Label(activity.getCategory()));
                    placeholder.setPrefSize(205, 185);
                    placeholder.setStyle("-fx-background-color: #E8EEEE; -fx-background-radius: 10;");
                    button.setGraphic(placeholder);
                }

                button.setOnAction(event -> openActivity(activity));
                contentPane.getChildren().add(button);
            }
        }

        private List<Activity> filterByCategory(String category) {
            List<Activity> filteredActivities = new ArrayList<>();

            for (Activity activity : activities) {
                if (activity.getCategory().equalsIgnoreCase(category)) {
                    filteredActivities.add(activity);
                }
            }
            return filteredActivities;
        }

        private Image loadImage(Activity activity) {
            if (activity.getImageLocation() == null) return null;

            URL imageUrl = getClass().getResource("/com/example/cab302project/Activities/" + activity.getImageLocation());

            if (imageUrl == null) return null;
            return new Image(imageUrl.toExternalForm(), 480, 220, true,true);
        }
        private void openActivity(Activity activity) {
            try{
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/cab302project/ActivityDetails.fxml"));

                Node detailspage = loader.load();

                Pane parent = (Pane) activityRoot.getParent();

                ActivitiesController controller = loader.getController();

                controller.setActivity(activity, parent, activityRoot);

                parent.getChildren().setAll(detailspage);
            }catch (IOException e ) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Unable to open activity details.").showAndWait();
            }
        }

        private void setActivity(Activity activity, Pane parent, Node previousPage) {
            selectedActivity = activity;
            mainContent = parent;
            browsePage = previousPage;

            activityName.setText(activity.getName());
            categoryLabel.setText(activity.getCategory());
            descriptionLabel.setText(activity.getDescription());
            goalLabel.setText("Suggested goal: " + activity.getGoal() + " mins");

            activityImage.setImage(loadImage(activity));
            imagePlaceholder.setText(activity.getCategory());
            imagePlaceholder.setVisible(activityImage.getImage() == null);
        }

        @FXML
        private void onBackClicked() {
            mainContent.getChildren().setAll(browsePage);
        }

        @FXML
        private void onSelectActivityClicked() {
            Toggle selected = durationGroup.getSelectedToggle();

            if (selected == null) {
                selectionMessage.setText("Please select a duration.");
                return;
            }

            String duration = ((RadioButton) selected).getText();

            if (selected == customDuration) {
                try {
                    int minutes = Integer.parseInt(customMinutes.getText().trim());

                    if (minutes <= 0) {
                        selectionMessage.setText("Enter a positive whole number of minutes.");
                        return;
                    }

                    duration = minutes + " mins";
                } catch (NumberFormatException e) {
                    selectionMessage.setText("Please enter a number.");
                    return;
                }
            }

            selectionMessage.setText(selectedActivity.getName()
                    + " selected for " + duration + ". Not saved yet.");
        }

    }



