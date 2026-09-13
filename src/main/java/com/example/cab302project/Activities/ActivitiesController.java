package com.example.cab302project.Activities;

import com.example.cab302project.Database.DatabaseActivityDAO;
import com.example.cab302project.Database.DatabaseConnection;
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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ActivitiesController implements Initializable {
    private List<Activity> activities;
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

            DatabaseActivityDAO dao = new DatabaseActivityDAO();

            activities = dao.LoadActivities();

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

                button.setStyle("-fx-background-color: white; -fx-border-color: #AAAAAA;" + "-fx-border-radius:15; -fx-background-radius: 15; -fx-cursor: hand;" + "-fx-font-weight: bold;");
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
            if (activity.getImageFile() == null) return null;

            URL imageUrl = getClass().getResource("/com/example/cab302project/Activities/" + activity.getImageFile());

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



