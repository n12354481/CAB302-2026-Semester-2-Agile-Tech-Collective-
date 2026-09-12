package com.example.cab302project.Activities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class ActivitiesController implements Initializable {

    List<Pane> panes = new ArrayList<>();

    @FXML
    private AnchorPane activityRoot;

    @FXML
    private AnchorPane detailsRoot;

    @FXML
    private AnchorPane all;

    @FXML
    private AnchorPane fitness;

    @FXML
    private AnchorPane social;

    @FXML
    private AnchorPane others;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (all != null) {
            panes.add(all);
            panes.add(fitness);
            panes.add(social);
            panes.add(others);
        }
    }

    //category navigation
    @FXML
    private void onAllButtonClicked() { show(all); }

    @FXML
    private void onFitnessButtonClicked() { show(fitness); }

    @FXML
    private void onSocialButtonClicked() { show(social); }

    @FXML
    private void onOthersButtonClicked() { show(others); }

    //fitness
    @FXML
    private void onSwimmingClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/cab302project/ActivityDetails.fxml"
                    )
            );

            Node detailsPage = loader.load();
            Pane mainContent = (Pane) activityRoot.getParent();
            mainContent.getChildren().setAll(detailsPage);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTennisClicked() { System.out.println("Tennis selected"); }

    @FXML
    private void onGymnasticsClicked() { System.out.println("Gymnastics selected"); }

    //social
    @FXML
    private void onEventClicked() { System.out.println("Attend an Event selected"); }

    @FXML
    private void onFriendClicked() { System.out.println("Meet a Friend selected"); }

    @FXML
    private void onClubClicked() { System.out.println("Join a Club selected"); }

    //others
    @FXML
    private void onStudyClicked() { System.out.println("Study Session selected"); }

    @FXML
    private void onChoresClicked() { System.out.println("Complete Chores selected"); }

    @FXML
    private void onMeditationClicked() { System.out.println("Meditation selected"); }

    //back button from activity details
    @FXML
    private void onBackClicked() {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/cab302project/ActivityMain.fxml"
                    )
            );

            Node activityPage = loader.load();
            Pane mainContent = (Pane) detailsRoot.getParent();
            mainContent.getChildren().setAll(activityPage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void show(AnchorPane pane) {

        for (Pane p : panes) {
            p.setManaged(false);
            p.setVisible(false);
        }

        pane.setVisible(true);
        pane.setManaged(true);
    }

}

