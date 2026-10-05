package com.example.cab302project.Activities;

import com.example.cab302project.Database.DatabaseActivityDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.*;

/**
 * controller for activities feature
 * handles activity browsing, category filtering, and activity details
 */
public class ActivitiesController implements Initializable {
    private List<Activity> activities;
    private Activity selectedActivity;

    private final IActivityDAO activityDAO = new DatabaseActivityDAO();

    // temporarily stores selected activities while app is running
    private final List<SelectedActivity> myActivities = new ArrayList<>();
    // temporarily stores completed activities
    private final List<SelectedActivity> recentActivities = new ArrayList<>();
    // controls whether remove buttons are shown
    private boolean editMode = false;

    private SelectedActivity activityBeingEdited = null;

    // file used to remember selected activities after closing app
    private static final String MY_ACTIVITIES_FILE = "myActivities.txt";

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


    /**
     * initialises the activities page when the FXML is loaded
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityDAO.addDefaultActivities();

        activities = activityDAO.getAllActivities();
        loadSavedActivities();
        createCategoryButtons();

        customMinutes.visibleProperty().bind(customDuration.selectedProperty());
        customMinutes.managedProperty().bind(customDuration.selectedProperty());
        customActivityCategory.getItems().addAll(activityDAO.getCategories());

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

        List<Activity> categoryActivities = activityDAO.getActivitiesByCategory(category);

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

        button.setPrefSize(230, 260);
        button.setMinSize(230, 260);
        button.setWrapText(true);
        button.setContentDisplay(ContentDisplay.TOP);
        button.setGraphicTextGap(10);
        button.setTooltip(new Tooltip(activity.getDescription()));

        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: gray;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 15;" +
                        "-fx-font-weight: bold;");

        StackPane placeholder = new StackPane();
        Label placeholderLabel = new Label(activity.getCategory());
        placeholder.getChildren().add(placeholderLabel);
        placeholder.setPrefSize(205, 185);
        placeholder.setStyle("-fx-background-color: #E8EEEE;" + "-fx-background-radius: 10;");
        button.setGraphic(placeholder);

        // opens selected activity details page
        button.setOnAction(event -> openActivity(activity));
        return button;
    }

    private Button createCustomActivityCard() {
        Button button = new Button("+\n\nCreate your\nown activity");
        button.setPrefSize(230, 260);
        button.setMinSize(230, 260);
        button.setWrapText(true);
        button.setAlignment(javafx.geometry.Pos.CENTER);

        button.setStyle("-fx-background-color: white;" +
                "-fx-border-color: gray;" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 15;" +
                "-fx-font-weight: bold;" +
                "-fx-text-alignment: center;");

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
    private void onCreateActivityClicked() {
        String name = customActivityName.getText().trim();
        String category = customActivityCategory.getValue();
        String description = customActivityDescription.getText().trim();

        if (name.isEmpty() || category == null) {
            createActivityMessage.setText("Please enter an activity name and category");
            return;
        }

        Activity newActivity = new Activity(name, category, description, 0);

        Activity savedActivity = activityDAO.getOrCreateActivity(newActivity);
        activities = activityDAO.getAllActivities();

        customActivityName.clear();
        customActivityCategory.setValue(null);
        customActivityDescription.clear();
        createActivityMessage.setText("");

        createActivityPane.setVisible(false);
        createActivityPane.setManaged(false);

        browsePane.setVisible(true);
        browsePane.setManaged(true);

        changeCategory(savedActivity.getCategory());
    }

    // opens details view and displays selected activity information
    // activity images have not been added yet. method will load them once available
    private void openActivity(Activity activity) {
        selectedActivity = activity;

        activityBeingEdited = null;
        selectActivityButton.setText("Select Activity");

        activityName.setText(activity.getName());
        categoryLabel.setText(activity.getCategory());
        descriptionLabel.setText(activity.getDescription());

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
                "Workshop",
                "STEM society event",
                "Meditation",
                "Review notes",
                "Coding practice",
                "Reading",
                "Gym workout",
                "Walk",
                "Uni club event",
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

        recentActivities.removeIf(selected -> selected.getActivity().getId() == activityId);

        saveMyActivities();

        activityDAO.delete(activityId);
        activities = activityDAO.getAllActivities();

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
            saveMyActivities();

            selectionMessage.setText(selectedActivity.getName() + " updated to " + minutes + " mins. ");
        } else {
            // checks whether the activity already exists before adding it to database
            activityDAO.getOrCreateActivity(selectedActivity);

            addToMyActivities(selectedActivity, minutes);

            selectionMessage.setText(selectedActivity.getName() + " added to My Activities for " + minutes + "mins.");
        }
    }

    private void saveMyActivities() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MY_ACTIVITIES_FILE))) {
            for (SelectedActivity selected : myActivities) {
                writer.println(selected.getActivity().getId() + "," + selected.getMinutes() + ",ACTIVE");
            }

            for (SelectedActivity selected : recentActivities) {
                writer.println(selected.getActivity().getId() + "," + selected.getMinutes() + ",COMPLETED");
            }
        } catch (IOException e) {
            System.out.println("Could not save activities." + e.getMessage());
        }
    }

    private void loadSavedActivities() {
        myActivities.clear();
        recentActivities.clear();

        File file = new File(MY_ACTIVITIES_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 2) {
                    continue;
                }

                try {
                    int activityId = Integer.parseInt(parts[0].trim());
                    int minutes = Integer.parseInt(parts[1].trim());
                    String status = parts.length >= 3 ? parts[2].trim() : "ACTIVE";

                    Activity activity = activityDAO.getActivityById(activityId);

                    if (activity == null) {
                        continue;
                    }

                    SelectedActivity saved = new SelectedActivity(activity, minutes);

                    if (status.equals("COMPLETED")) {
                        recentActivities.add(saved);
                    } else {
                        myActivities.add(saved);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Could not read activity");
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load activities: " + e.getMessage());
        }
    }

    private void addToMyActivities(Activity activity, int minutes) {
        for (SelectedActivity selected : myActivities) {
            if (selected.getActivity().getName().equalsIgnoreCase(activity.getName())) {
                selected.setMinutes(minutes);

                saveMyActivities();
                return;
            }
        }
        myActivities.add(new SelectedActivity(activity, minutes));
        saveMyActivities();
    }

    @FXML
    private void onMyActivitiesClicked() {
        browsePane.setVisible(false);
        browsePane.setManaged(false);

        detailsPane.setVisible(false);
        detailsPane.setManaged(false);

        myActivitiesPane.setVisible(true);
        myActivitiesPane.setManaged(true);

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
                saveMyActivities();
                loadMyActivities();
            });

            row.getChildren().addAll(editActivityButton, removeButton);
        }

        return row;
    }

    private void completedActivity(SelectedActivity selected) {

        myActivities.remove(selected);

        if (!recentActivities.contains(selected)) {
            recentActivities.add(0, selected);
        }
        saveMyActivities();
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
                recentActivities.remove(selected);

                if (!myActivities.contains(selected)) {
                    myActivities.add(selected);
                }

                saveMyActivities();
                loadMyActivities();
            }
        });
        return row;
    }

    // stores activity selected by user and their chosen duration
    private static class SelectedActivity {
        private final Activity activity;
        private int minutes;

        public SelectedActivity(Activity activity, int minutes) {
            this.activity = activity;
            this.minutes = minutes;
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
    }
}







