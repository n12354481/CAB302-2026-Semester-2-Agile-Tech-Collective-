package com.example.cab302project.Rewards;

import com.example.cab302project.Database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The rewards panel: stored days, streak, stage reached, and a strip showing each day.
 *
 * Sparing on purpose, every number comes from {@link RewardsService}, which is where the
 * logic and the tests reside. This class only decides what to show.
 *
 * Two things here are demo scaffolding until the rest of the app catches up. The user is
 * hardcoded because there is no login, and a claim is only remembered for as long as the
 * window is open because there is no table to store it in.
 */
public class RewardsController {

    /** The panel always shows the same user. */
    private static final int DEMO_USER_ID = 1;

    /** A fortnight that matches the seeded run. */
    private static final int WINDOW_DAYS = 14;

    /** Sprout and Flower. The other four stages are still unnamed. */
    private static final int[] THRESHOLDS = {3, 15};

    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("d MMM");

    @FXML private Label rangeLabel;
    @FXML private VBox emptyState;
    @FXML private VBox summaryState;
    @FXML private Label stageLabel;
    @FXML private Label balancedLabel;
    @FXML private Label streakLabel;
    @FXML private HBox dayStrip;
    @FXML private Button claimButton;
    @FXML private Label claimLabel;
    @FXML private Label messageLabel;

    private RewardsService service;
    private LocalDate today;

    /** Stages claimed so far. Resets when the window closes. */
    private int stagesClaimed;

    @FXML
    public void initialize() {
        today = LocalDate.now();
        try {
            Connection connection = DatabaseConnection.getInstance();
            service = new RewardsService(connection, THRESHOLDS);
        } catch (RuntimeException ex) {
            showError("Could not open the database: " + ex.getMessage());
            return;
        }
        refresh();
    }

    @FXML
    private void onLoadDemo() {
        try {
            Connection connection = DatabaseConnection.getInstance();
            GardenDemoData.ensureUser(connection, DEMO_USER_ID);
            GardenDemoData.seed(connection, DEMO_USER_ID, today);
            refresh();
        } catch (SQLException ex) {
            showError("Could not load the demo data: " + ex.getMessage());
        }
    }

    @FXML
    private void onClaim() {
        stagesClaimed++;
        refresh();
    }

    /** Reads the summary again and redraws the garden. Called on open and after anything changes. */
    private void refresh() {
        LocalDate from = today.minusDays(WINDOW_DAYS - 1L);
        rangeLabel.setText(from.format(DAY_MONTH) + " to " + today.format(DAY_MONTH));

        List<DayState> days;
        RewardsSummary summary;
        try {
            days = service.statesFor(DEMO_USER_ID, from, today,
                    RewardsService.DEFAULT_ACTIVITY_GOAL, RewardsService.DEFAULT_REST_GOAL);
            summary = RewardsSummary.of(days, THRESHOLDS);
        } catch (SQLException ex) {
            showError("Could not read your logged days: " + ex.getMessage());
            return;
        }

        messageLabel.setText("");
        boolean nothingLogged = days.stream().allMatch(day -> day == DayState.NOTHING_LOGGED);
        show(emptyState, nothingLogged);
        show(summaryState, !nothingLogged);

        if (nothingLogged) {
            return;
        }

        stageLabel.setText(describeStage(summary.stageReached()));
        balancedLabel.setText(summary.balancedDays() + " balanced "
                + plural(summary.balancedDays(), "day", "days") + " banked"
                + describeNextStage(summary.balancedDays()));
        streakLabel.setText(summary.currentStreak() + " "
                + plural(summary.currentStreak(), "day", "days") + " in a row");

        drawDayStrip(days, from);
        updateClaim(summary);
    }

    private void updateClaim(RewardsSummary summary) {
        int owed = summary.unclaimedStages(stagesClaimed);
        claimButton.setDisable(owed == 0);
        if (owed > 0) {
            claimLabel.setText(owed + " " + plural(owed, "stage", "stages") + " ready to claim");
        } else if (stagesClaimed > 0) {
            claimLabel.setText(stagesClaimed + " claimed");
        } else {
            claimLabel.setText("Nothing to claim yet");
        }
    }

    /** One small block per day, oldest is on the left, hover for the date and what it is. */
    private void drawDayStrip(List<DayState> days, LocalDate from) {
        dayStrip.getChildren().clear();
        for (int i = 0; i < days.size(); i++) {
            DayState state = days.get(i);
            LocalDate date = from.plusDays(i);

            Region block = new Region();
            block.setPrefSize(22, 22);
            block.setStyle("-fx-background-color: " + colourFor(state)
                    + "; -fx-background-radius: 3;");
            Tooltip.install(block, new Tooltip(date.format(DAY_MONTH) + " — " + describe(state)));
            dayStrip.getChildren().add(block);
        }
    }

    private static String colourFor(DayState state) {
        return switch (state) {
            case NOTHING_LOGGED -> "#e0e0e0";
            case REST_ONLY -> "#b3c6e7";
            case UNDER_BOTH -> "#d9d2b0";
            case FLOWERED_SHALLOW -> "#e8c07d";
            case ROOTED_NO_FLOWER -> "#8fae7b";
            case BALANCED, CAPPED -> "#4a7c3f";
        };
    }

    private static String describe(DayState state) {
        return switch (state) {
            case NOTHING_LOGGED -> "nothing logged";
            case REST_ONLY -> "rest only";
            case UNDER_BOTH -> "under both goals";
            case FLOWERED_SHALLOW -> "active, short on rest";
            case ROOTED_NO_FLOWER -> "rested, short on activity";
            case BALANCED -> "balanced";
            case CAPPED -> "balanced, over the cap";
        };
    }

    private static String describeStage(int stage) {
        return switch (stage) {
            case 0 -> "Not sprouted yet";
            case 1 -> "Stage 1 — Sprout";
            default -> "Stage " + stage + " — Flower";
        };
    }

    /** How far off the next threshold is */
    private static String describeNextStage(int balancedDays) {
        for (int threshold : THRESHOLDS) {
            if (balancedDays < threshold) {
                int toGo = threshold - balancedDays;
                return ", " + toGo + " more for the next stage";
            }
        }
        return "";
    }

    private static String plural(int count, String one, String many) {
        return count == 1 ? one : many;
    }

    private void show(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        show(emptyState, false);
        show(summaryState, false);
    }
}
