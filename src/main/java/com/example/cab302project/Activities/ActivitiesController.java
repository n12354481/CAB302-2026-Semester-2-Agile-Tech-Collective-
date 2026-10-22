package com.example.cab302project.Activities;

import com.example.cab302project.Database.DatabaseActivityDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javafx.geometry.Pos;

/**
 * controller for activities feature
 * handles activity browsing, category filtering, and activity details
 */
public class ActivitiesController implements Initializable {
    private List<Activity> activities;
    private Activity selectedActivity;

    private final IActivityDAO activityDAO = new DatabaseActivityDAO();

    private final DatabaseActivityDAO userActivityDAO = new DatabaseActivityDAO();

    private int userId;

    // temporarily stores selected activities while app is running
    private final List<SelectedActivity> myActivities = new ArrayList<>();

    private final List<SelectedActivity> recentActivities = new ArrayList<>();
    // controls whether remove buttons are shown
    private boolean editMode = false;

    private SelectedActivity activityBeingEdited = null;

    private File selectedImageFile;

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
    private Label categoryBannerLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label goalLabel;

    @FXML
    private ImageView activityDetailsImage;

    @FXML
    private ToggleGroup durationGroup;

    @FXML
    private RadioButton duration15;

    @FXML
    private RadioButton duration30;

    @FXML
    private RadioButton customDuration;

    @FXML
    private TextField customMinutes;

    @FXML
    private Label selectionMessage;

    @FXML
    private Button deleteActivityButton;

    @FXML
    private ScrollPane myActivitiesPane;

    @FXML
    private VBox myActivitiesList;

    @FXML
    private VBox recentActivitiesList;

    @FXML
    private Button editButton;

    @FXML
    private Button selectActivityButton;

    @FXML
    private ScrollPane createActivityPane;

    @FXML
    private TextField customActivityName;

    @FXML
    private ComboBox<String> customActivityCategory;

    @FXML
    private TextArea customActivityDescription;

    @FXML
    private Label createActivityMessage;

    @FXML
    private Label selectedImageLabel;

    @FXML
    private ImageView customImagePreview;

    @FXML
    private StackPane detailsImageFrame;


    /**
     * initialises the activities page when the FXML is loaded
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityDAO.addDefaultActivities();

        activities = activityDAO.getAllActivities();
        createCategoryButtons();

        customMinutes.visibleProperty().bind(customDuration.selectedProperty());
        customMinutes.managedProperty().bind(customDuration.selectedProperty());
        customActivityCategory.getItems().addAll(activityDAO.getCategories());

    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadCompletedActivities();
        if (!buttonBar.getButtons().isEmpty()) {
            Button firstButton = (Button) buttonBar.getButtons().get(0);
            changeCategory(firstButton.getText());
        }
    }

    private void loadCompletedActivities() {
        recentActivities.clear();
        List<int[]> logs = activityDAO.getActivityLogs(userId);

        for (int[] log : logs) {
            int logId = log[0];
            int activityId = log[1];
            int minutes = log[2];

            Activity activity = activityDAO.getActivityById(activityId);

            if (activity != null) {
                recentActivities.add(new SelectedActivity(activity, minutes, logId));
            }
        }
    }

    // creates category buttons based on category found in activity list
    private void createCategoryButtons() {
        List<String> categories = activityDAO.getCategories();

        for (String category : categories) {
            Button button = new Button(category);
            button.setOnAction(event -> changeCategory(category));
            buttonBar.getButtons().add(button);
        }

        // displays the first category when first page opens
        if (!categories.isEmpty()) {
            changeCategory(categories.get(0));
        }
    }

    // updates displayed activities when different category is selected
    private void changeCategory(String category) {
        contentPane.getChildren().clear();

        for (javafx.scene.Node node : buttonBar.getButtons()) {
            Button button = (Button) node;
            if (button.getText().equals(category)) {
                button.setStyle("-fx-background-color: #1F6F64;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;");
            } else {
                button.setStyle("-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;");
            }
        }

        List<Activity> categoryActivities = userActivityDAO.getActivitiesForUser(userId, category);

        // creates card for each activity in selected category
        for (Activity activity : categoryActivities) {
            contentPane.getChildren().add(createActivityCard(activity));
        }

        contentPane.getChildren().add(createCustomActivityCard());
    }

    /**
     * creates clickable card for an activity
     *
     * @param activity activity shown on card
     * @return button containing the activity information
     */
    private Button createActivityCard(Activity activity) {
        Button button = new Button(activity.getName());

        button.setPrefSize(310, 340);
        button.setMinSize(310, 340);
        button.setWrapText(true);
        button.setContentDisplay(ContentDisplay.TOP);
        button.setGraphicTextGap(10);

        button.getStyleClass().add("activity-card");

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(285, 260);
        imageContainer.setMinSize(285, 260);
        imageContainer.setMaxSize(285, 260);
        imageContainer.getStyleClass().add("activity-image-container");

        Image image = ActivityImages.load(activity);

        if (image != null) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(285);
            imageView.setFitHeight(260);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);

            imageContainer.getChildren().add(imageView);
        } else {
            Label placeholder =  new Label(activity.getCategory());
            imageContainer.getChildren().add(placeholder);
        }
        button.setGraphic(imageContainer);
        button.setOnAction(event -> {openActivity(activity);});
        return button;
    }

    private Button createCustomActivityCard() {
        Button button = new Button("+\n\nCreate your\nown activity");
        button.setPrefSize(310, 340);
        button.setMinSize(310, 340);
        button.setWrapText(true);
        button.setAlignment(javafx.geometry.Pos.CENTER);

        button.getStyleClass().add("create-activity-card");

        button.setOnAction(event -> openCreateActivityPage());

        return button;
    }

    // create custom activity
    private void openCreateActivityPage() {
        browsePane.setVisible(false);
        browsePane.setManaged(false);

        detailsPane.setVisible(false);
        detailsPane.setManaged(false);

        myActivitiesPane.setVisible(false);
        myActivitiesPane.setManaged(false);

        createActivityPane.setVisible(true);
        createActivityPane.setManaged(true);

        createActivityMessage.setText("");
    }

    @FXML
    private void onCreateActivitiyBackClicked() {
        createActivityPane.setVisible(false);
        createActivityPane.setManaged(false);

        browsePane.setVisible(true);
        browsePane.setManaged(true);
    }

    @FXML
    private void onChooseActivityImageClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose an Image");

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(createActivityPane.getScene().getWindow());

        if (file != null) {
            Image preview = new Image(file.toURI().toString(), false);

            if (preview.isError()) {
                createActivityMessage.setText("Could not open the selected image.");
                return;
            }

            selectedImageFile = file;
            selectedImageLabel.setText(file.getName());
            customImagePreview.setImage(preview);
        }
    }

    @FXML
    private void onCreateActivityClicked() {
        String name = customActivityName.getText().trim();
        String category = customActivityCategory.getValue();
        String description = customActivityDescription.getText().trim();

        if (name.isEmpty() || category == null) {
            createActivityMessage.setText("Please enter an activity name and category");
            return;
        }

        String imageFile = null;
        if (selectedImageFile != null) {
            try {
                imageFile = ActivityImages.saveUpload(selectedImageFile);
            } catch (IOException e) {
                createActivityMessage.setText("Could not save image: " + e.getMessage());
                return;
            }
        }

        Activity newActivity = new Activity(name, category, description, 0, imageFile);

        int newId = userActivityDAO.insertForUser(newActivity, userId);
        if (newId <= 0) {
            createActivityMessage.setText("Could not save activity");
            return;
        }

        Activity savedActivity = activityDAO.getActivityById(newId);

        customActivityName.clear();
        customActivityCategory.setValue(null);
        customActivityDescription.clear();

        selectedImageFile = null;
        selectedImageLabel.setText("No image selected");
        customImagePreview.setImage(null);

        createActivityMessage.setText("");

        createActivityPane.setVisible(false);
        createActivityPane.setManaged(false);

        browsePane.setVisible(true);
        browsePane.setManaged(true);

        changeCategory(savedActivity.getCategory());
    }

    private void displayDetailsImage(Activity activity) {
        Image image = ActivityImages.load(activity);
        activityDetailsImage.setImage(image);

        if (image != null && image.getWidth() > 0 && image.getHeight() > 0) {
            Runnable updateImage = () -> {
                double width = detailsImageFrame.getWidth();
                double height = detailsImageFrame.getHeight();

                if (width <= 0 || height <= 0) return;

                double scale = Math.max(width / image.getWidth(), height / image.getHeight());
                double cropWidth = width / scale;
                double cropHeight = height / scale;
                double x = (image.getWidth() - cropWidth) / 2;
                double y = (image.getHeight() - cropHeight) / 2;

                activityDetailsImage.setViewport(new javafx.geometry.Rectangle2D(x, y, cropWidth, cropHeight));

                activityDetailsImage.setFitWidth(width);
                activityDetailsImage.setFitHeight(height);
                activityDetailsImage.setPreserveRatio(false);
                activityDetailsImage.setSmooth(true);
            };
            detailsImageFrame.widthProperty().addListener((obs, oldWidth, newWidth) -> updateImage.run());
            detailsImageFrame.heightProperty().addListener((obs, oldHeight, newHeight) -> updateImage.run());

            javafx.application.Platform.runLater(updateImage);
        } else {
            activityDetailsImage.setImage(null);
            activityDetailsImage.setViewport(null);
        }
        boolean noImage = image ==  null;

        categoryBannerLabel.setVisible(noImage);
        categoryBannerLabel.setManaged(noImage);
    }

    // opens details view and displays selected activity information
    // activity images have not been added yet. method will load them once available
    private void openActivity(Activity activity) {
        selectedActivity = activity;

        activityBeingEdited = null;
        selectActivityButton.setText("Select Activity");

        activityName.setText(activity.getName());
        categoryLabel.setText(activity.getCategory());
        categoryBannerLabel.setText(selectedActivity.getCategory());
        descriptionLabel.setText(activity.getDescription());

        displayDetailsImage(activity);

        if (activity.getGoal() > 0) {
            goalLabel.setText("Suggested goal: " +
                    activity.getGoal() +
                    " mins");
            goalLabel.setVisible(true);
            goalLabel.setManaged(true);
        } else {
            goalLabel.setVisible(false);
            goalLabel.setManaged(false);
        }

        boolean customActivity = !isDefaultActivity(activity);

        deleteActivityButton.setVisible(customActivity);
        deleteActivityButton.setManaged(customActivity);

        // clears previous duration selections
        durationGroup.selectToggle(null);
        customMinutes.clear();
        selectionMessage.setText("");

        browsePane.setVisible(false);
        browsePane.setManaged(false);

        detailsPane.setVisible(true);
        detailsPane.setManaged(true);

        myActivitiesPane.setVisible(false);
        myActivitiesPane.setManaged(false);
    }

    private boolean isDefaultActivity(Activity activity) {
        List<String> defaultActivities = List.of(
                "Swimming",
                "Jogging",
                "Tennis",
                "Study with a friend",
                "Attend a workshop",
                "Join a STEM society event",
                "Meditation",
                "Review lecture notes",
                "Coding Practice",
                "Reading",
                "Gym workout",
                "Go for a walk",
                "University club event",
                "Lunch with a friend"
        );
        return defaultActivities.contains(activity.getName());
    }

    @FXML
    private void onDeleteActivityClicked() {
        if (selectedActivity == null) {
            return;
        }
        if (isDefaultActivity(selectedActivity)) {
            return;
        }

        String category = selectedActivity.getCategory();
        int activityId = selectedActivity.getId();

        myActivities.removeIf(selected -> selected.getActivity().getId() == activityId);

        userActivityDAO.deleteForUser(activityId, userId);

        loadCompletedActivities();

        selectedActivity = null;
        detailsPane.setVisible(false);
        detailsPane.setManaged(false);
        browsePane.setVisible(true);
        browsePane.setManaged(true);

        changeCategory(category);
    }

    private void openActivityForEditing(SelectedActivity selected) {
        activityBeingEdited = selected;
        selectedActivity = selected.getActivity();

        selectActivityButton.setText("Save Changes");

        activityName.setText(selectedActivity.getName());

        categoryLabel.setText(selectedActivity.getCategory());

        categoryBannerLabel.setText(selectedActivity.getCategory());

        descriptionLabel.setText(selectedActivity.getDescription());

        displayDetailsImage(selectedActivity);

        if (selectedActivity.getGoal() > 0) {
            goalLabel.setText("Suggested goal: " + selectedActivity.getGoal() + " mins");

            goalLabel.setVisible(true);
            goalLabel.setManaged(true);
        } else {
            goalLabel.setVisible(false);
            goalLabel.setManaged(false);
        }

        deleteActivityButton.setVisible(false);
        deleteActivityButton.setManaged(false);

        if (selected.getMinutes() == 15) {
            durationGroup.selectToggle(duration15);
        } else if (selected.getMinutes() == 30) {
            durationGroup.selectToggle(duration30);
        } else {
            durationGroup.selectToggle(customDuration);
            customMinutes.setText(String.valueOf(selected.getMinutes()));
        }

        selectionMessage.setText("");

        myActivitiesPane.setVisible(false);
        myActivitiesPane.setManaged(false);

        browsePane.setVisible(false);
        browsePane.setManaged(false);

        detailsPane.setVisible(true);
        detailsPane.setManaged(true);

    }

    // returns user from details view to activity browsing view
    @FXML
    private void onBackClicked() {
        detailsPane.setVisible(false);
        detailsPane.setManaged(false);

        if (activityBeingEdited != null) {
            activityBeingEdited = null;
            selectActivityButton.setText("Select Activity");

            myActivitiesPane.setVisible(true);
            myActivitiesPane.setManaged(true);

            loadMyActivities();
        } else {
            browsePane.setVisible(true);
            browsePane.setManaged(true);
        }
    }

    // checks the chosen duration and selects current activity
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
        } else if (selected == duration15) {
            minutes = 15;
        } else if (selected == duration30) {
            minutes = 30;
        } else {
            selectionMessage.setText("Please select a duration.");
            return;
        }

        if (activityBeingEdited != null) {
            activityBeingEdited.setMinutes(minutes);

            selectionMessage.setText(selectedActivity.getName() + " updated to " + minutes + " mins. ");
        } else {
            // checks whether the activity already exists before adding it to database
            addToMyActivities(selectedActivity, minutes);

            selectionMessage.setText(selectedActivity.getName() + " added to My Activities for " + minutes + "mins.");
        }
    }

    private void addToMyActivities(Activity activity, int minutes) {
        for (SelectedActivity selected : myActivities) {
            if (selected.getActivity().getId() == activity.getId()) {
                selected.setMinutes(minutes);

                return;
            }
        }
        myActivities.add(new SelectedActivity(activity, minutes));
    }

    @FXML
    private void onMyActivitiesClicked() {
        browsePane.setVisible(false);
        browsePane.setManaged(false);

        detailsPane.setVisible(false);
        detailsPane.setManaged(false);

        myActivitiesPane.setVisible(true);
        myActivitiesPane.setManaged(true);

        loadCompletedActivities();
        loadMyActivities();
    }

    @FXML
    private void onAddActivityClicked() {
        myActivitiesPane.setVisible(false);
        myActivitiesPane.setManaged(false);

        detailsPane.setVisible(false);
        detailsPane.setManaged(false);

        browsePane.setVisible(true);
        browsePane.setManaged(true);
    }

    @FXML
    private void onMyActivitiesBackClicked() {
        myActivitiesPane.setVisible(false);
        myActivitiesPane.setManaged(false);

        browsePane.setVisible(true);
        browsePane.setManaged(true);
    }

    @FXML
    private void onEditActivitiesClicked() {
        editMode = !editMode;
        if (editMode) {
            editButton.setText("Done");
        } else {
            editButton.setText("Edit");
        }

        loadMyActivities();
    }

    private void loadMyActivities() {
        myActivitiesList.getChildren().clear();
        recentActivitiesList.getChildren().clear();

        for (SelectedActivity selected : myActivities) {
            myActivitiesList.getChildren().add(createMyActivityRow(selected));
        }
        if (myActivities.isEmpty()) {
            Label emptyLabel = new Label("You have not selected any activities yet.");

            emptyLabel.setStyle("-fx-text-fill: gray");

            myActivitiesList.getChildren().add(emptyLabel);
        }

        for (SelectedActivity completed : recentActivities) {
            recentActivitiesList.getChildren().add(createRecentActivityRow(completed));
        }

        if (recentActivities.isEmpty()) {
            Label emptyLabel = new Label("No completed activities yet.");

            emptyLabel.setStyle("-fx-text-fill: gray");

            recentActivitiesList.getChildren().add(emptyLabel);
        }
    }

    private HBox createMyActivityRow(SelectedActivity selected) {
        CheckBox completedCheckBox = new CheckBox();

        Label nameLabel = new Label(selected.getActivity().getName());

        nameLabel.setStyle("-fx-font-size: 15px;" + "-fx-font-weight: bold;");

        Label durationLabel = new Label(selected.getMinutes() + "mins");

        durationLabel.setStyle("-fx-font-size: 14px;" + "-fx-text-fill: gray");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, completedCheckBox, nameLabel, spacer, durationLabel);

        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        row.setStyle("-fx-background-color: white;" + "-fx-border-color: #DDDDDD;" + "-fx-border-radius: 10;" + "-fx-background-radius: 10;" + "-fx-padding: 12;");

        completedCheckBox.setOnAction(event -> {
            if (completedCheckBox.isSelected()) {
                completedActivity(selected);
            }
        });

        if (editMode) {
            Button editActivityButton = new Button("Edit");

            editActivityButton.setStyle("-fx-background-color: #DEF3F0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;");

            editActivityButton.setOnAction(event -> openActivityForEditing(selected));

            Button removeButton = new Button("Remove");

            removeButton.setStyle("-fx-background-color: #DEF3F0;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;");

            removeButton.setOnAction(event -> {
                myActivities.remove(selected);
                loadMyActivities();
            });

            row.getChildren().addAll(editActivityButton, removeButton);
        }

        return row;
    }

    private void completedActivity(SelectedActivity selected) {

        if (userId <= 0) {
            System.out.println("Cannot save activity because user ID has not been set.");
            return;
        }
        int logId = activityDAO.insertActivityLog(
                userId,
                selected.getActivity().getId(),
                LocalDate.now().toString(),
                selected.getMinutes()
        );

        if (logId <= 0) {
            System.out.println("Cannot save completed activity.");
            return;
        }

        selected.setLogId(logId);

        myActivities.remove(selected);

        if(!recentActivities.contains(selected)) {
            recentActivities.add(0, selected);
        }

        loadMyActivities();

    }

    private HBox createRecentActivityRow(SelectedActivity selected) {
        CheckBox completedCheckBox = new CheckBox();

        completedCheckBox.setSelected(true);

        completedCheckBox.setStyle("-fx-mark-color: white;" + "-fx-accent: #1F6F64;");

        Label nameLabel = new Label(selected.getActivity().getName());

        Label durationLabel = new Label(selected.getMinutes() + "mins");

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, completedCheckBox, nameLabel, spacer, durationLabel);

        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        row.setStyle("-fx-background-color: gray;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 12;");

        completedCheckBox.setOnAction(event -> {
            if (!completedCheckBox.isSelected()) {
                if (selected.getLogId() > 0) {
                    activityDAO.deleteActivityLog(selected.getLogId(), userId);
                }

                recentActivities.remove(selected);
                selected.setLogId(-1);

                if (!myActivities.contains(selected)) {
                    myActivities.add(selected);
                }
            }
            loadMyActivities();

        });
        return row;
    }

    // stores activity selected by user and their chosen duration
    private static class SelectedActivity {
        private final Activity activity;
        private int minutes;

        private int logId = -1;

        public SelectedActivity(Activity activity, int minutes) {
            this.activity = activity;
            this.minutes = minutes;
        }

        public SelectedActivity(Activity activity, int minutes, int logId) {
            this.activity = activity;
            this.minutes = minutes;
            this.logId = logId;
        }

        public Activity getActivity() {
            return activity;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getLogId() { return logId; }

        public void setLogId(int logId) { this.logId = logId; }
    }
}