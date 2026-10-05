package com.example.cab302project.MoodForm;

import com.example.cab302project.HelloController;
import com.example.cab302project.Database.DatabaseCheckInDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckInController {

    @FXML
    private ToggleGroup emotionGroup;

    @FXML
    private ToggleGroup sleepGroup;

    @FXML
    private RadioButton strugglingRadio;

    @FXML
    private RadioButton notGreatRadio;

    @FXML
    private RadioButton okayRadio;

    @FXML
    private RadioButton goodRadio;

    @FXML
    private RadioButton greatRadio;

    @FXML
    private RadioButton noSleepRadio;

    @FXML
    private RadioButton lessSleepRadio;

    @FXML
    private RadioButton goodSleepRadio;

    @FXML
    private RadioButton excellentSleepRadio;

    @FXML
    private ComboBox<String> waterComboBox;

    @FXML
    private Slider stressSlider;

    @FXML
    private CheckBox happyCheckBox;

    @FXML
    private CheckBox calmCheckBox;

    @FXML
    private CheckBox tiredCheckBox;

    @FXML
    private CheckBox anxiousCheckBox;

    @FXML
    private CheckBox sadCheckBox;

    @FXML
    private CheckBox sleepyCheckBox;

    @FXML
    private Label messageLabel;

    private final DatabaseCheckInDAO checkInDAO =
            new DatabaseCheckInDAO();

    private int userID;
    private HelloController helloController;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setHelloController(HelloController helloController) {
        this.helloController = helloController;
    }

    @FXML
    public void initialize() {

        waterComboBox.getItems().addAll(
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "5+"
        );
    }


    @FXML
    private void handleSubmit() {

        if (userID <= 0) {
            showError("Unable to identify the logged-in user.");
            return;
        }

        // Make sure required questions have been answered

        if (emotionGroup.getSelectedToggle() == null) {
            showError("Please select how you are feeling.");
            return;
        }

        if (sleepGroup.getSelectedToggle() == null) {
            showError("Please select how much sleep you had.");
            return;
        }

        if (waterComboBox.getValue() == null) {
            showError("Please select your water intake.");
            return;
        }


        // Convert form selections into database values

        int emotionToday = getEmotionValue();
        int sleep = getSleepValue();
        int water = getWaterValue();

        int studyStress =
                (int) Math.round(stressSlider.getValue());

        List<String> moods = getSelectedMoods();


        // Create the CheckIn object

        CheckIn checkIn = new CheckIn(
                userID,
                LocalDate.now(),
                emotionToday,
                sleep,
                water,
                studyStress,
                moods
        );


        // Save to database

        boolean saved =
                checkInDAO.saveCheckIn(checkIn);


        if (saved) {

            messageLabel.setStyle(
                    "-fx-text-fill: green; " +
                            "-fx-font-weight: bold;"
            );

            messageLabel.setText(
                    "Check-in saved successfully!"
            );

            System.out.println(
                    "Check-in saved. ID: "
                            + checkIn.getCheckinID()
            );

            if (helloController != null) {
                helloController.loadWellbeingPage();
            }

        } else {

            showError(
                    "Unable to save check-in."
            );
        }
    }


    private int getEmotionValue() {

        if (strugglingRadio.isSelected()) {
            return 1;
        }

        if (notGreatRadio.isSelected()) {
            return 2;
        }

        if (okayRadio.isSelected()) {
            return 3;
        }

        if (goodRadio.isSelected()) {
            return 4;
        }

        if (greatRadio.isSelected()) {
            return 5;
        }

        return 0;
    }


    private int getSleepValue() {

        if (noSleepRadio.isSelected()) {
            return 0;
        }

        if (lessSleepRadio.isSelected()) {
            return 1;
        }

        if (goodSleepRadio.isSelected()) {
            return 2;
        }

        if (excellentSleepRadio.isSelected()) {
            return 3;
        }

        return 0;
    }


    private int getWaterValue() {

        String selectedWater =
                waterComboBox.getValue();

        if ("5+".equals(selectedWater)) {
            return 6;
        }

        return Integer.parseInt(selectedWater);
    }


    private List<String> getSelectedMoods() {

        List<String> moods =
                new ArrayList<>();

        if (happyCheckBox.isSelected()) {
            moods.add("Happy");
        }

        if (calmCheckBox.isSelected()) {
            moods.add("Calm");
        }

        if (tiredCheckBox.isSelected()) {
            moods.add("Tired");
        }

        if (anxiousCheckBox.isSelected()) {
            moods.add("Anxious");
        }

        if (sadCheckBox.isSelected()) {
            moods.add("Sad");
        }

        if (sleepyCheckBox.isSelected()) {
            moods.add("Sleepy");
        }

        return moods;
    }


    private void showError(String message) {

        messageLabel.setStyle(
                "-fx-text-fill: red;"
        );

        messageLabel.setText(message);
    }
}