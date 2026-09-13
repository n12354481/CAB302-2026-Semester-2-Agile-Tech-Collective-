package com.example.cab302project.Rewards;

import com.example.cab302project.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The claiming page
 *
 * The rewards are hardcoded for this iteration. Nothing counts real activities yet,
 * it is forgotten when the window closes.
 */
public class ClaimsController {

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMMM");
    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("d MMM");

    @FXML private Label monthLabel;

    @FXML private VBox readyBand;
    @FXML private Label readyHeading;
    @FXML private GridPane readyList;

    @FXML private VBox countingBand;
    @FXML private Label countingHeading;
    @FXML private GridPane countingList;

    @FXML private VBox claimedBand;
    @FXML private Label claimedHeading;
    @FXML private FlowPane claimedList;

    @FXML private StackPane overlay;
    @FXML private Label dialogTitle;
    @FXML private Label dialogText;

    private final List<Reward> rewards = demoRewards();

    /** The reward the confirm dialog is asking for, null when the dialog is closed. */
    private Reward pending;

    @FXML
    public void initialize() {
        monthLabel.setText(LocalDate.now().format(MONTH));
        draw();
    }

    /** Sorts every reward into its group and updates the three counts. */
    private void draw() {
        readyList.getChildren().clear();
        countingList.getChildren().clear();
        claimedList.getChildren().clear();

        for (Reward reward : rewards) {
            if (reward.claimed()) {
                claimedList.getChildren().add(claimedPill(reward));
            } else if (reward.complete()) {
                addToGrid(readyList, readyCard(reward));
            } else {
                addToGrid(countingList, countingCard(reward));
            }
        }

        readyHeading.setText("READY TO CLAIM (" + readyList.getChildren().size() + ")");
        countingHeading.setText("STILL COUNTING (" + countingList.getChildren().size() + ")");
        claimedHeading.setText("CLAIMED (" + claimedList.getChildren().size() + ")");

        // An empty band is a heading and a rule so its hidden.
        show(readyBand, !readyList.getChildren().isEmpty());
        show(countingBand, !countingList.getChildren().isEmpty());
        show(claimedBand, !claimedList.getChildren().isEmpty());
    }

    /**
     * Fills a grid left to right, wrapping onto the next row when the columns run out.
     */
    private void addToGrid(GridPane grid, Node card) {
        int columns = Math.max(1, grid.getColumnConstraints().size());
        int placed = grid.getChildren().size();
        grid.add(card, placed % columns, placed / columns);
    }

    /** Grey card with a Claim button. The target is met with reward waiting. */
    private Node readyCard(Reward reward) {
        Label name = new Label(reward.name());
        name.getStyleClass().add("reward-name");
        Label counts = new Label(reward.counts());
        counts.getStyleClass().add("reward-counts");
        Label progress = new Label(reward.progress());
        progress.getStyleClass().add("progress-complete");

        Button claim = new Button("Claim");
        claim.getStyleClass().add("claim-button");
        claim.setOnAction(event -> askToClaim(reward));

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        HBox bottom = new HBox(progress, gap, claim);
        bottom.setAlignment(Pos.CENTER_LEFT);

        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);

        VBox card = new VBox(4, name, counts, grow, bottom);
        card.getStyleClass().add("ready-card");
        // Fill the column, so a full row of cards reaches both edges.
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    /** White card with a progress bar. Still counting towards target. */
    private Node countingCard(Reward reward) {
        Label name = new Label(reward.name());
        name.getStyleClass().add("reward-name");
        Label counts = new Label(reward.counts());
        counts.getStyleClass().add("reward-counts");

        ProgressBar bar = new ProgressBar(reward.fraction());
        bar.setMaxWidth(Double.MAX_VALUE);

        Label progress = new Label(reward.progress());
        progress.getStyleClass().add("progress-counting");

        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);

        VBox card = new VBox(4, name, counts, grow, bar, progress);
        card.getStyleClass().add("counting-card");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    /** Pill with the date it was claimed on. */
    private Node claimedPill(Reward reward) {
        Label tick = new Label("✓");
        tick.getStyleClass().add("claimed-tick");
        Label name = new Label(reward.name());
        name.getStyleClass().add("claimed-name");
        Label when = new Label(whenClaimed(reward));
        when.getStyleClass().add("claimed-when");

        HBox pill = new HBox(8, tick, name, when);
        pill.setAlignment(Pos.CENTER_LEFT);
        pill.getStyleClass().add("claimed-pill");
        return pill;
    }

    private String whenClaimed(Reward reward) {
        return reward.claimedOn().equals(LocalDate.now())
                ? "just now"
                : reward.claimedOn().format(DAY_MONTH);
    }

    /**
     * A cosmetic is something you keep, so it asks before it is added. A milestone
     * unlocks itself. None on the page yet.
     */
    private void askToClaim(Reward reward) {
        pending = reward;
        dialogTitle.setText("Claim the " + lowerFirst(reward.name()) + "?");
        dialogText.setText(reward.description());
        show(overlay, true);
    }

    @FXML
    private void onCancel() {
        pending = null;
        show(overlay, false);
    }

    @FXML
    private void onConfirm() {
        if (pending == null) {
            return;
        }
        pending.claim(LocalDate.now());
        pending = null;

        show(overlay, false);
        draw();
    }

    @FXML
    private void onBack() {
        swapPage("Rewards.fxml");
    }

    /** The nav keeps every page in one StackPane, so swapping its child changes page. */
    private void swapPage(String page) {
        Node holder = monthLabel.getScene().lookup("#mainContent");
        if (!(holder instanceof StackPane pane)) {
            return;
        }
        try {
            Node loaded = FXMLLoader.load(HelloApplication.class.getResource(page));
            pane.getChildren().setAll(loaded);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** "Garden gnome" reads better */
    private static String lowerFirst(String text) {
        return text.isEmpty() ? text : Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    private void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    /** Hardcoded for this iteration */
    private static List<Reward> demoRewards() {
        Reward greenThumb = new Reward("“Green Thumb” title", "Balanced days", 20, 20, "days",
                "Earned for twenty balanced days. Added to your profile once you confirm.");
        greenThumb.claim(LocalDate.of(2026, 8, 12));

        return new ArrayList<>(List.of(
                new Reward("Garden gnome", "Months active", 1, 1, "month",
                        "Earned for your first full month with Stem Wellbeing. Added to your "
                                + "garden once you confirm."),
                new Reward("“Early Bird” title", "Morning study sessions", 10, 10, "before 9am",
                        "Earned for ten study sessions started before 9am. Added to your profile "
                                + "once you confirm."),
                new Reward("Skies (morning, noon, night)", "Balanced days", 7, 20, "days",
                        "Earned for twenty balanced days. Changes the sky behind your garden "
                                + "once you confirm."),
                new Reward("Jacaranda", "Study sessions", 7, 10, "sessions",
                        "Earned for ten study sessions. Planted in your garden once you confirm."),
                new Reward("Eucalyptus", "Physical movement", 9, 15, "sessions",
                        "Earned for fifteen movement sessions. Planted in your garden once you "
                                + "confirm."),
                greenThumb));
    }
}
