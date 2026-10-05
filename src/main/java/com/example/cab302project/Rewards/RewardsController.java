package com.example.cab302project.Rewards;

import com.example.cab302project.Database.DatabaseConnection;
import com.example.cab302project.HelloApplication;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.OptionalInt;

/**
 * The rewards dashboard with the garden next to
 * the selected day, then the rewards ready to claim and the streak.
 *
 * Every number comes from {@link RewardsService}, which is where the
 * logic and the tests reside. This class only decides what to show.
 *
 * The claiming page it links to is still hardcoded.
 */
public class RewardsController {

    /** A fortnight that matches the preconfigured run */
    private static final int WINDOW_DAYS = 14;

    /** First two stages. The other four stages are still unnamed. */
    private static final int[] THRESHOLDS = {3, 15};

    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("d MMM");
    private static final DateTimeFormatter WEEKDAY_DAY_MONTH = DateTimeFormatter.ofPattern("EEE d MMM");

    @FXML private Label rangeLabel;
    @FXML private VBox summaryState;

    @FXML private VBox gardenCard;
    @FXML private HBox legend;

    /** Added in code; see the note in Rewards.fxml. */
    private final GardenPane garden = new GardenPane();

    @FXML private Label selectedDateLabel;
    @FXML private Label selectedStateLabel;
    @FXML private Label aboveLabel;
    @FXML private VBox activityList;
    @FXML private Label capNote;
    @FXML private Label belowLabel;
    @FXML private VBox restList;
    @FXML private Label goalLabel;

    @FXML private Label readyToClaimLabel;

    @FXML private Label streakNumber;
    @FXML private Label streakUnit;
    @FXML private Region streakDivider;
    @FXML private HBox milestoneRow;
    @FXML private Label milestoneValue;
    @FXML private Label startStreakLabel;

    @FXML private Label messageLabel;

    private RewardsService service;
    private LocalDate today;
    private int userId;

    /** The day on the selected card. Starts today and moves when a plant is clicked. */
    private final ObjectProperty<LocalDate> selectedDay = new SimpleObjectProperty<>();

    @FXML
    public void initialize() {
        today = LocalDate.now();
        selectedDay.set(today);
        gardenCard.getChildren().add(garden);
        legend.getChildren().setAll(GardenPane.legendItems());
        garden.setOnPick(selectedDay::set);
        try {
            Connection connection = DatabaseConnection.getInstance();
            service = new RewardsService(connection, THRESHOLDS);
        } catch (RuntimeException ex) {
            showError("Could not open the database: " + ex.getMessage());
            return;
        }
        selectedDay.addListener((obs, old, day) -> refresh());
    }

    /**
     * Shows the rewards for the logged-in user. Called by the nav once the page is loaded.
     * @param userId: The userID of the logged-in user.
     */
    public void setUserId(int userId) {
        this.userId = userId;
        if (service != null) {
            refresh();
        }
    }

    @FXML
    private void onViewClaiming() {
        // The nav shell keeps every page in one StackPane, so swapping its child changes page.
        Node holder = messageLabel.getScene().lookup("#mainContent");
        if (!(holder instanceof StackPane pane)) {
            messageLabel.setText("The claiming page only opens from inside the app's nav.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Claims.fxml"));
            Node claims = loader.load();
            ClaimsController controller = loader.getController();
            controller.setUserId(userId);
            pane.getChildren().setAll(claims);
        } catch (IOException ex) {
            messageLabel.setText("Could not open the claiming page: " + ex.getMessage());
        }
    }

    /** Reads everything again and redraws. Called on open and after anything changes. */
    private void refresh() {
        LocalDate from = today.minusDays(WINDOW_DAYS - 1L);
        LocalDate selected = selectedDay.get();
        rangeLabel.setText(from.format(DAY_MONTH) + " – " + today.format(DAY_MONTH));

        List<DayTotals> totals;
        List<DayEntry> activities;
        List<DayEntry> rest;
        try {
            totals = service.totalsFor(userId, from, today);
            activities = service.activitiesOn(userId, selected);
            rest = service.restOn(userId, selected);
        } catch (SQLException ex) {
            showError("Could not read your logged days: " + ex.getMessage());
            return;
        }

        List<DayState> days = totals.stream()
                .map(day -> day.state(RewardsService.DEFAULT_ACTIVITY_GOAL, RewardsService.DEFAULT_REST_GOAL))
                .toList();
        RewardsSummary summary = RewardsSummary.of(days, THRESHOLDS);

        messageLabel.setText("");
        show(summaryState, true);

        garden.draw(totals, days, today, selected,
                RewardsService.DEFAULT_ACTIVITY_GOAL, RewardsService.DEFAULT_REST_GOAL);

        DayState selectedState = days.get((int) ChronoUnit.DAYS.between(from, selected));
        showSelectedDay(selectedState, activities, rest);

        // Nothing records a claim yet.
        readyToClaimLabel.setText(String.valueOf(summary.unclaimedStages(0)));

        showStreak(summary.currentStreak());
    }

    /** What grew above the ground and below it on the selected day, and the goals measured. */
    private void showSelectedDay(DayState state, List<DayEntry> activities, List<DayEntry> rest) {
        selectedDateLabel.setText(selectedDay.get().format(WEEKDAY_DAY_MONTH));
        selectedStateLabel.setText(describe(state));

        int activityMinutes = activities.stream().mapToInt(DayEntry::minutes).sum();
        int countedActivity = Math.min(activityMinutes, DayState.ACTIVITY_CAP_MINUTES);
        aboveLabel.setText(("Above the ground (" + duration(countedActivity) + ")").toUpperCase());

        activityList.getChildren().clear();
        for (DayEntry entry : activities) {
            activityList.getChildren().add(entryRow(entry.name(), duration(entry.minutes())));
        }
        if (activities.isEmpty()) {
            activityList.getChildren().add(nothingLogged());
        }

        capNote.setText("Counted to your " + DayState.ACTIVITY_CAP_MINUTES + " min daily cap");
        show(capNote, activityMinutes > DayState.ACTIVITY_CAP_MINUTES);

        int restMinutes = rest.stream().mapToInt(DayEntry::minutes).sum();
        belowLabel.setText(("Below the ground (" + duration(restMinutes) + ")").toUpperCase());

        restList.getChildren().clear();
        for (DayEntry entry : rest) {
            restList.getChildren().add(entryRow(entry.name(), duration(entry.minutes())));
        }
        if (rest.isEmpty()) {
            restList.getChildren().add(nothingLogged());
        }

        goalLabel.setText("Goal that day: " + duration(RewardsService.DEFAULT_ACTIVITY_GOAL)
                + " activity, " + duration(RewardsService.DEFAULT_REST_GOAL) + " rest");
    }

    /** The count, then either the next milestone or a nudge to start one. */
    private void showStreak(int streak) {
        streakNumber.setText(String.valueOf(streak));
        streakUnit.setText(plural(streak, "day", "days") + " in a row");

        OptionalInt next = StreakMilestone.next(streak);
        next.ifPresent(milestone -> milestoneValue.setText(milestone + " days"));
        show(milestoneRow, next.isPresent());
        show(startStreakLabel, streak == 0);
        // Past the last milestone there is nothing left under the line.
        show(streakDivider, next.isPresent() || streak == 0);
    }

    private static HBox entryRow(String name, String detail) {
        Label tick = new Label("✓");
        tick.getStyleClass().add("tick");
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("entry-name");
        Label detailLabel = new Label(detail);
        detailLabel.getStyleClass().add("entry-detail");

        HBox row = new HBox(10, tick, new VBox(nameLabel, detailLabel));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private static Label nothingLogged() {
        Label label = new Label("Nothing logged");
        label.getStyleClass().add("entry-detail");
        return label;
    }

    private static String describe(DayState state) {
        return switch (state) {
            case NOTHING_LOGGED -> "Nothing logged";
            case REST_ONLY -> "Rest only";
            case UNDER_BOTH -> "Under both goals";
            case FLOWERED_SHALLOW -> "Active, short on rest";
            case ROOTED_NO_FLOWER -> "Rested, short on activity";
            case BALANCED -> "Balanced";
            case CAPPED -> "Balanced, over the cap";
        };
    }

    /** 20 min, 2 hr, 7 hr 20 min. Shared with the garden's goal labels. */
    static String duration(int minutes) {
        int hours = minutes / 60;
        int leftover = minutes % 60;
        if (hours == 0) {
            return leftover + " min";
        }
        if (leftover == 0) {
            return hours + " hr";
        }
        return hours + " hr " + leftover + " min";
    }

    private static String plural(int count, String one, String many) {
        return count == 1 ? one : many;
    }

    private void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        show(summaryState, false);
    }
}
