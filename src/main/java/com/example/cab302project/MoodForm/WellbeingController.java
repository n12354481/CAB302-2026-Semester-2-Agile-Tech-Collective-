package com.example.cab302project.MoodForm;

import com.example.cab302project.Database.DatabaseCheckInDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class WellbeingController {

    private int userID;

    private final DatabaseCheckInDAO checkInDAO =
            new DatabaseCheckInDAO();

    @FXML
    private Label checkInCountLabel;

    public void setUserID(int userID) {
        this.userID = userID;
        loadWellbeingData();
    }

    private void loadWellbeingData() {

        List<CheckIn> checkIns =
                checkInDAO.getCheckInsForUser(userID);

        checkInCountLabel.setText(
                "Total check-ins: " + checkIns.size()
        );

        System.out.println(
                "Loaded " + checkIns.size()
                        + " check-ins for user "
                        + userID
        );
    }
}