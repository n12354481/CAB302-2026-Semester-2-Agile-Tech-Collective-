package com.example.cab302project.MoodForm;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ComboBox;

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

    @FXML
    private void handleSubmit() {
        System.out.println("Submit clicked");
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
}