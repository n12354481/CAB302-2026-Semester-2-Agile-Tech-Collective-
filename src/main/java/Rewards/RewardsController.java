package Rewards;

import Database.DatabaseConnection;
import App.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The rewards dashboard with the garden next to
 * the selected day, then the rewards ready to claim and the streak grid.
 *
 * Every number comes from {@link RewardsService}, which is where the
 * logic and the tests reside. This class only decides what to show.
 *
 * Some of it is demo scaffolding until the rest of the app is built. The user is
 * hardcoded because there is no login, the garden is a  placeholder, and the
 * claiming page it links to is hardcoded too.
 */
public class RewardsController {

    /** The panel always shows the same user. The claiming page uses it too. */
    static final int DEMO_USER_ID = 1;

    /** A fortnight that matches the preconfigured run */
    private static final int WINDOW_DAYS = 14;

    /** First two stages. The other four stages are still unnamed. */
    private static final int[] THRESHOLDS = {3, 15};

    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("d MMM");
    private static final DateTimeFormatter WEEKDAY_DAY_MONTH = DateTimeFormatter.ofPattern("EEE d MMM");

    /** Grid headings, Monday is initially like {@link java.time.DayOfWeek}. */
    private static final String[] WEEKDAYS = {"M", "T", "W", "T", "F", "S", "S"};

    /** The key, best day first. CAPPED is left out because it shares BALANCED's colour. */
    private static final DayState[] KEY = {
            DayState.BALANCED, DayState.ROOTED_NO_FLOWER, DayState.FLOWERED_SHALLOW,
            DayState.UNDER_BOTH, DayState.REST_ONLY, DayState.NOTHING_LOGGED};

    @FXML private Label rangeLabel;
    @FXML private VBox emptyState;
    @FXML private VBox summaryState;

    @FXML private Label selectedDateLabel;
    @FXML private Label aboveLabel;
    @FXML private VBox activityList;
    @FXML private Label capNote;
    @FXML private Label belowLabel;
    @FXML private VBox restList;
    @FXML private Label goalLabel;

    @FXML private Label readyToClaimLabel;

    @FXML private Label streakNumber;
    @FXML private Label streakUnit;
    @FXML private VBox keyList;
    @FXML private GridPane dayGrid;

    @FXML private Label messageLabel;

    private RewardsService service;
    private LocalDate today;

    /** The day on the selected card. Starts today and moves when a grid cell is clicked. */
    private LocalDate selected;

    @FXML
    public void initialize() {
        today = LocalDate.now();
        selected = today;
        drawKey();
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
    private void onViewClaiming() {
        // The nav shell keeps every page in one StackPane, so swapping its child changes page.
        Node holder = messageLabel.getScene().lookup("#mainContent");
        if (!(holder instanceof StackPane pane)) {
            messageLabel.setText("The claiming page only opens from inside the app's nav.");
            return;
        }
        try {
            Node claims = FXMLLoader.load(HelloApplication.class.getResource("Claims.fxml"));
            pane.getChildren().setAll(claims);
        } catch (IOException ex) {
            messageLabel.setText("Could not open the claiming page: " + ex.getMessage());
        }
    }

    /** Reads everything again and redraws. Called on open and after anything changes. */
    private void refresh() {
        LocalDate from = today.minusDays(WINDOW_DAYS - 1L);
        rangeLabel.setText(from.format(DAY_MONTH) + " – " + today.format(DAY_MONTH));

        List<DayState> days;
        RewardsSummary summary;
        List<DayEntry> activities;
        List<DayEntry> rest;
        try {
            days = service.statesFor(DEMO_USER_ID, from, today,
                    RewardsService.DEFAULT_ACTIVITY_GOAL, RewardsService.DEFAULT_REST_GOAL);
            summary = RewardsSummary.of(days, THRESHOLDS);
            activities = service.activitiesOn(DEMO_USER_ID, selected);
            rest = service.restOn(DEMO_USER_ID, selected);
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

        showSelectedDay(activities, rest);

        // Nothing records a claim yet.
        readyToClaimLabel.setText(String.valueOf(summary.unclaimedStages(0)));

        streakNumber.setText(String.valueOf(summary.currentStreak()));
        streakUnit.setText(plural(summary.currentStreak(), "day", "days") + " in a row");
        drawDayGrid(days, from);
    }

    /** What grew above the ground and below it on the selected day, and the goals measured. */
    private void showSelectedDay(List<DayEntry> activities, List<DayEntry> rest) {
        selectedDateLabel.setText(selected.format(WEEKDAY_DAY_MONTH));

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

    /**
     * Click a cell to show that day in the selected card.
     */
    private void drawDayGrid(List<DayState> days, LocalDate from) {
        dayGrid.getChildren().clear();
        for (int column = 0; column < WEEKDAYS.length; column++) {
            Label heading = new Label(WEEKDAYS[column]);
            heading.getStyleClass().add("weekday");
            dayGrid.add(heading, column, 0);
        }

        int offset = from.getDayOfWeek().getValue() - 1;
        for (int i = 0; i < days.size(); i++) {
            DayState state = days.get(i);
            LocalDate date = from.plusDays(i);
            int position = offset + i;

            Region cell = new Region();
            cell.getStyleClass().add("day-cell");
            cell.setStyle("-fx-background-color: " + colourFor(state) + ";");
            if (date.equals(selected)) {
                cell.getStyleClass().add("selected");
            }
            Tooltip.install(cell, new Tooltip(date.format(DAY_MONTH) + " — " + describe(state)));
            cell.setOnMouseClicked(event -> {
                selected = date;
                refresh();
            });
            dayGrid.add(cell, position % 7, position / 7 + 1);
        }

        // Today acts as the last day, so the marker goes on the row under the last cell.
        int todayPosition = offset + days.size() - 1;
        Label marker = new Label("↑ today");
        marker.getStyleClass().add("today-marker");
        marker.setMinWidth(Region.USE_PREF_SIZE);
        dayGrid.add(marker, todayPosition % 7, todayPosition / 7 + 2);
    }

    /** The colour key beside the grid (drawn once). */
    private void drawKey() {
        for (DayState state : KEY) {
            Region swatch = new Region();
            swatch.getStyleClass().add("key-swatch");
            swatch.setStyle("-fx-background-color: " + colourFor(state) + ";");
            Label label = new Label(describe(state));
            label.getStyleClass().add("key-label");

            HBox row = new HBox(7, swatch, label);
            row.setAlignment(Pos.CENTER_LEFT);
            keyList.getChildren().add(row);
        }
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

    private static String colourFor(DayState state) {
        return switch (state) {
            // Greyscale. The better the day, the darker the grey.
            case NOTHING_LOGGED -> "#eceeed";
            case REST_ONLY -> "#d6d9d8";
            case UNDER_BOTH -> "#bfc3c2";
            case FLOWERED_SHALLOW -> "#9a9f9e";
            case ROOTED_NO_FLOWER -> "#6f7473";
            case BALANCED, CAPPED -> "#3a3d3c";
        };
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

    /** 20 min, 2 hr, 7 hr 20 min. */
    private static String duration(int minutes) {
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
        show(emptyState, false);
        show(summaryState, false);
    }
}
