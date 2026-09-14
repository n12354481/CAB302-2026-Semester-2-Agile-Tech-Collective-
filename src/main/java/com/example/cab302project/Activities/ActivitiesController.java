package com.example.cab302project.Activities;

import com.example.cab302project.Database.DatabaseActivityDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ActivitiesController implements Initializable {
    private List<Activity> activities;
    private Activity selectedActivity;

    private final DatabaseActivityDAO activityDAO = new DatabaseActivityDAO();

    @FXML
    private VBox browsePane;

    @FXML
    private ButtonBar buttonBar;

    @FXML
    private FlowPane contentPane;

    @FXML
    private ScrollPane detailsPane;

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
    private RadioButton duration15;

    @FXML
    private RadioButton duration30;

    @FXML
    private  RadioButton customDuration;

    @FXML
    private TextField customMinutes;

    @FXML
    private Label selectionMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDefaultActivities();
        createCategoryButtons();

        customMinutes.visibleProperty().bind(customDuration.selectedProperty());
        customMinutes.managedProperty().bind(customDuration.selectedProperty());
    }

    /**
     * loads the activities
     *
     */

    private void loadDefaultActivities() {

        activities = new ArrayList<>();

        activities.add(new Activity("Swimming", "Fitness", "Swimming is a full body physical activity that can help improve fitness while also giving you a break from studying and sitting for long periods of time. It can be done at your own pace, whether you want to swim a few relaxed laps or have more active workout.", 30, null));

        activities.add(new Activity("Jogging", "Fitness", "Jogging is a simple way to stay physically active and take some time away from studying or sitting at a desk. You can jog around campus, your neighbourhood or a nearby park at a pace that feels comfortable. It can also be a good way to clear your mind and have a break after spending a long time working on university tasks.",30,null));

        activities.add(new Activity("Tennis", "Fitness", "Tennis is an active sport that can help improve fitness, coordination and concentration. It can be played casually or competitively with another person, making it a good way to exercise while also spending time with friends and taking a break from studying.", 30, null));

        activities.add(new Activity("Study with a friend", "Social", "Studying with a friend gives you the chance to work through university content together, discuss difficult topics and help each other when something is confusing. It can make studying feel less isolating and can also help you stay motivated and focused on what you need to complete.", 30, null));

        activities.add(new Activity("Attend a workshop", "Social", "Attend a workshop to learn something new or develop skills outside of your normal classes. Workshops can give you practical experience, introduce you to different topics and provide an opportunity to meet other students who may have similar academic or career interests.", 30, null));

        activities.add(new Activity("Join a STEM society event", "Social", "Take part in an event organised by a STEM-related student society at university. These events can be a good way to meet students with similar interests, learn more about different areas of STEM and get involved with the university community outside of classes.", 30, null));

        activities.add(new Activity("Meditation", "Others", "Meditation is a simple activity where you take some time away from studying and other distractions to slow down and focus on the present moment. Even a short meditation session can give you some quiet time to relax, clear your mind and reset before continuing with your day.", 30, null));

        activities.add(new Activity("Review lecture notes", "Others", "Spend some time going back through notes from your recent lectures or tutorials to refresh your understanding of the content. Regularly reviewing notes can help you identify topics you are unsure about and avoid leaving all of your revision until right before an assessment or exam.", 30, null));

        activities.add(new Activity("Coding Practice", "Others", "Spend some time practising programming outside of your required classwork. You could work through coding exercises, practise concepts you found difficult in class or experiment with a small problem. Regular practice can help you become more comfortable with programming and problem solving over time.", 30, null));

        activities.add(new Activity("Reading", "Others", "Take some time to read something you enjoy outside of your usual university work. This could be a novel, short story, magazine or another topic that interests you. Reading can be a relaxing way to spend some time away from assignments, coding and screens.", 30, null));

        activities.add(new Activity("Gym workout", "Fitness", "Complete a gym workout based on your own fitness level and goals. This could include strength training, cardio or a combination of different exercises. Going to the gym can help you stay physically active, especially when a lot of your university work involves sitting at a desk or computer.", 30, null));

        activities.add(new Activity("Go for a walk", "Others", "Take a break from your desk and go for a walk around campus, your neighbourhood or somewhere outdoors. Walking is a simple way to get some movement into your day and can give you a chance to clear your head after spending a long time studying or working on an assignment.", 30, null));

        activities.add(new Activity("University club event", "Social", "Attend an event organised by one of the university's student clubs or societies. It is an opportunity to take a break from academic work, try something different and meet other students who share similar interests. It can also help you feel more involved in university life outside of classes.", 30, null));

        activities.add(new Activity("Lunch with a friend", "Social", "Take some time away from studying to have lunch with a friend or classmate. It gives you a chance to catch up, talk about things outside of university work and have a proper break during a busy day instead of spending the whole day studying by yourself.", 30, null));

    }

    private void createCategoryButtons() {
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

        private void changeCategory(String category) {
            contentPane.getChildren().clear();

            for (javafx.scene.Node node: buttonBar.getButtons()) {
                Button button = (Button) node;
                if (button.getText().equals(category)) {
                    button.setStyle("-fx-background-color: #386F65;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 10;");
                }else {
                    button.setStyle("-fx-background-color: #E8EEEE;" +
                                    "-fx-text-fill: black;" +
                                    "-fx-background-radius: 10;");
                }
            }

            for (Activity activity : filterByCategory(category)) {
                Button activityButton = createActivityCard(activity);
                contentPane.getChildren().add(activityButton);
            }
        }

        private Button createActivityCard(Activity activity) {
        Button button = new Button(activity.getName());
        button.setPrefSize(230, 260);
        button.setMinSize(230, 260);
        button.setWrapText(true);
        button.setContentDisplay(ContentDisplay.TOP);
        button.setGraphicTextGap(10);
        button.setTooltip(new Tooltip(activity.getDescription()));

        button.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #AAAAAA;" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 15;" +
                "-fx-font-weight: bold;");

        Image image = loadImage(activity);
        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(185);
            imageView.setFitWidth(205);
            imageView.setPreserveRatio(true);
            button.setGraphic(imageView);
        }else{
            StackPane placeholder = new StackPane();
            Label placeholderLabel = new Label(activity.getCategory());
            placeholder.getChildren().add(placeholderLabel);
            placeholder.setPrefSize(205, 185);
            placeholder.setStyle("-fx-background-color: #E8EEEE;" + "-fx-background-radius: 10;");
            button.setGraphic(placeholder);
        }
        button.setOnAction(event -> openActivity(activity));
        return button;
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
        if (activity.getImageFile() == null
                || activity.getImageFile().isBlank()) {
            return null;
        }
        URL imageUrl = getClass().getResource("/com/example/cab302project/Activities/" + activity.getImageFile());
        if (imageUrl == null) { return null; }

        return new Image(imageUrl.toExternalForm(), 480, 220, true, true);
    }

        private void openActivity(Activity activity) {
            selectedActivity = activity;
            activityName.setText(activity.getName());
            categoryLabel.setText(activity.getCategory());
            descriptionLabel.setText(activity.getDescription());
            goalLabel.setText("Suggested goal: " + activity.getGoal() + " mins");

            Image image = loadImage(activity);
            activityImage.setImage(image);

            if(image == null) {
                imagePlaceholder.setText(activity.getCategory());
                imagePlaceholder.setVisible(true);
            }else{
                imagePlaceholder.setVisible(false);
            }
            durationGroup.selectToggle(null);
            customMinutes.clear();
            selectionMessage.setText("");

            browsePane.setVisible(false);
            browsePane.setManaged(false);
            detailsPane.setVisible(true);
            detailsPane.setManaged(true);
        }

        @FXML
        private void onBackClicked() {
            detailsPane.setVisible(false);
            detailsPane.setManaged(false);
            browsePane.setVisible(true);
            browsePane.setManaged(true);
        }

        @FXML
        private void onSelectActivityClicked() {
            Toggle selected = durationGroup.getSelectedToggle();
            if (selected == null) {
                selectionMessage.setText("Please select a duration.");
                return;
            }

            int minutes;

            if (selected == customDuration) {
                try {
                    minutes = Integer.parseInt(customMinutes.getText().trim());
                    if (minutes <= 0) {
                        selectionMessage.setText("Enter number of minutes.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    selectionMessage.setText("Please enter a number.");
                    return;
                }
            }else {
                if (selected == duration15) {
                    minutes = 15;
                } else if (selected == duration30) {
                    minutes = 30;
                } else {
                    selectionMessage.setText("Please select a duration.");
                    return;
                }
            }

            activityDAO.getOrCreateActivity(selectedActivity);

            selectionMessage.setText(selectedActivity.getName()
                    + " selected for " + minutes + "mins.");
        }
    }



