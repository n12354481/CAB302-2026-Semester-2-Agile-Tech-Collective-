package com.example.cab302project.Activities;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class ActivitiesController implements Initializable {

    List<Pane> panes = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        panes.add(all);
        panes.add(fitness);
        panes.add(study);
        panes.add(social);
        panes.add(others);

    }

    @FXML
    private AnchorPane all;

    @FXML
    private AnchorPane fitness;

    @FXML
    private AnchorPane study;

    @FXML
    private AnchorPane social;

    @FXML
    private AnchorPane others;

    //category navigation
    @FXML
    private void onAllButtonClicked() { show(all); }

    @FXML
    private void onFitnessButtonClicked() { show(fitness); }

    @FXML
    private void onStudyButtonClicked() { show(study); }

    @FXML
    private void onSocialButtonClicked() { show(social); }

    @FXML
    private void onOthersButtonClicked() { show(others); }

    private void show(AnchorPane pane) {

        for (Pane p : panes) {
            p.setManaged(false);
            p.setVisible(false);
        }

        pane.setVisible(true);
        pane.setManaged(true);
    }

}

