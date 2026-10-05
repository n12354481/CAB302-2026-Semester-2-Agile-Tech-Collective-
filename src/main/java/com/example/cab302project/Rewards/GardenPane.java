package com.example.cab302project.Rewards;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.example.cab302project.Rewards.GardenGeometry.GROUND_Y;
import static com.example.cab302project.Rewards.GardenGeometry.HEIGHT;
import static com.example.cab302project.Rewards.GardenGeometry.LEFT;
import static com.example.cab302project.Rewards.GardenGeometry.RIGHT;
import static com.example.cab302project.Rewards.GardenGeometry.WIDTH;

/**
 * The garden: one plant per day, activity as the stem above the soil and rest as the
 * roots below it.
 *
 * <p>Everything is drawn into a fixed 740 x 340 pane at the numbers in
 * {@link GardenGeometry}, then that pane is scaled to fit the card. All the maths lives
 * there; this class only turns the numbers into shapes.
 */
public class GardenPane extends StackPane {

    private static final Color STEM = Color.web("#2F7D68");
    private static final Color LEAF = Color.web("#D6EBE1");
    private static final Color PETAL = Color.web("#E9F4EE");
    private static final Color ROOT = Color.web("#8A7660");
    private static final Color SOIL = Color.web("#F2EEE7");
    private static final Color SOIL_LINE = Color.web("#C9BFAF");
    private static final Color SEED = Color.web("#9a9f9e");
    private static final Color DIVIDER = Color.web("#e4e6e5");

    // See-through versions of #E3F0EA and the screen grey, so the goal lines and soil show through.
    private static final Color SELECTED = Color.web("#2F7D68", 0.13);
    private static final Color HOVER = Color.web("#1b1d1d", 0.05);

    private static final double SOIL_DEPTH = 112;
    private static final double STUB_HEIGHT = 8;
    private static final double REST_GOAL_Y = GROUND_Y + GardenGeometry.REST_GOAL_DEPTH;
    private static final double ACTIVITY_GOAL_Y = GROUND_Y - GardenGeometry.ACTIVITY_GOAL_HEIGHT;

    private static final String[] WEEKDAYS = {"M", "T", "W", "T", "F", "S", "S"};

    private final Pane canvas = new Pane();

    private Consumer<LocalDate> onPick = date -> { };

    // The range and selection from the last draw, for the arrow keys.
    private LocalDate first;
    private LocalDate last;
    private LocalDate selected;

    public GardenPane() {
        canvas.setMinSize(WIDTH, HEIGHT);
        canvas.setPrefSize(WIDTH, HEIGHT);
        canvas.setMaxSize(WIDTH, HEIGHT);

        // Scale from the top left corner to the card's width. The Group reports the scaled
        // size, so the card grows and shrinks in height to match.
        Scale scale = new Scale(1, 1, 0, 0);
        canvas.getTransforms().add(scale);
        widthProperty().addListener((obs, old, width) -> {
            scale.setX(width.doubleValue() / WIDTH);
            scale.setY(width.doubleValue() / WIDTH);
        });
        getChildren().add(new Group(canvas));
        setAlignment(Pos.TOP_LEFT);
        setMinWidth(0);

        setFocusTraversable(true);
        addEventHandler(KeyEvent.KEY_PRESSED, this::onKey);
    }

    /** Called with the day clicked, or moved to with the arrow keys. */
    public void setOnPick(Consumer<LocalDate> onPick) {
        this.onPick = onPick;
    }

    /**
     * Redraws the whole garden.
     *
     * @param days   the minutes for each day, oldest first. The last one is today.
     * @param states the state of each day, in the same order
     */
    public void draw(List<DayTotals> days, List<DayState> states, LocalDate today,
                     LocalDate selected, int activityGoal, int restGoal) {
        this.first = days.get(0).date();
        this.last = days.get(days.size() - 1).date();
        this.selected = selected;

        int count = days.size();
        double columnWidth = GardenGeometry.columnWidth(count);
        double soilBottom = GROUND_Y + SOIL_DEPTH;
        boolean empty = GardenGeometry.nothingPlanted(states);

        List<Node> nodes = new ArrayList<>();

        Rectangle soil = new Rectangle(LEFT, GROUND_Y, RIGHT - LEFT, SOIL_DEPTH);
        soil.setFill(SOIL);
        nodes.add(soil);

        // A faint line before each Monday. Only the soil part when the garden is empty.
        for (int i = 1; i < count; i++) {
            if (days.get(i).date().getDayOfWeek() == DayOfWeek.MONDAY) {
                double x = LEFT + i * columnWidth;
                nodes.add(line(x, empty ? GROUND_Y : 8, x, soilBottom, DIVIDER, 1));
            }
        }

        if (!empty) {
            nodes.add(goalLine(ACTIVITY_GOAL_Y, STEM, 0.45));
            nodes.add(goalLine(REST_GOAL_Y, ROOT, 0.55));
            nodes.add(gutterLabel(RewardsController.duration(activityGoal) + " goal",
                    "garden-goal-activity", ACTIVITY_GOAL_Y, Pos.CENTER_RIGHT));
            nodes.add(gutterLabel(RewardsController.duration(restGoal) + " goal",
                    "garden-goal-rest", REST_GOAL_Y, Pos.CENTER_RIGHT));
            nodes.add(gutterLabel("ACTIVITY", "garden-axis", 14, Pos.CENTER_LEFT));
            nodes.add(gutterLabel("REST", "garden-axis", GROUND_Y + 14, Pos.CENTER_LEFT));
        }

        nodes.add(line(LEFT, GROUND_Y, RIGHT, GROUND_Y, SOIL_LINE, 1.5));

        for (int i = 0; i < count; i++) {
            nodes.add(day(i, count, days.get(i), states.get(i), today, activityGoal, restGoal));
        }

        if (empty) {
            nodes.add(emptyMessage());
        }

        canvas.getChildren().setAll(nodes);
    }

    /** The row at the top of the card: what each part of a plant means. */
    public static List<Node> legendItems() {
        Group root = new Group(line(6, 0, 6, 13, ROOT, 1.6),
                line(6, 4, 1, 10, ROOT, 1.2), line(6, 6, 11, 11, ROOT, 1.2));
        return List.of(
                legendItem(line(0, 0, 0, 13, STEM, 2.6), "Activity"),
                legendItem(root, "Rest"),
                legendItem(flower(0, 0), "Both goals met"),
                legendItem(seed(0, 0), "Nothing logged"));
    }

    /**
     * One day's column. The first child is the rectangle that takes the clicks and shows
     * the selected or hovered tint; the plant and the day labels sit on top of it.
     */
    private Group day(int i, int count, DayTotals day, DayState state, LocalDate today,
                      int activityGoal, int restGoal) {
        LocalDate date = day.date();
        double columnWidth = GardenGeometry.columnWidth(count);
        double columnLeft = LEFT + i * columnWidth;
        double x = GardenGeometry.plantX(i, count);
        boolean isSelected = date.equals(selected);

        Rectangle column = new Rectangle(columnLeft + 2, 6, columnWidth - 4, HEIGHT - 8);
        column.setArcWidth(12);
        column.setArcHeight(12);
        column.setFill(isSelected ? SELECTED : Color.TRANSPARENT);

        Group group = new Group(column);
        group.setCursor(Cursor.HAND);
        group.setOnMouseEntered(event -> column.setFill(isSelected ? SELECTED : HOVER));
        group.setOnMouseExited(event -> column.setFill(isSelected ? SELECTED : Color.TRANSPARENT));
        group.setOnMouseClicked(event -> {
            requestFocus();
            onPick.accept(date);
        });

        // Alternate the bend so the row does not look stamped out.
        double bend = i % 2 == 0 ? 1 : -1;

        double depth = GardenGeometry.rootDepth(day.restMinutes(), restGoal);
        if (depth > 0) {
            addRoots(group, x, depth, bend);
        }

        double height = GardenGeometry.stemHeight(day.activityMinutes(), activityGoal);
        double top = GROUND_Y - height;
        switch (GardenGeometry.topFor(state)) {
            case SEED -> group.getChildren().add(seed(x, GROUND_Y - 7));
            case STUB -> group.getChildren().add(line(x, GROUND_Y, x, GROUND_Y - STUB_HEIGHT, STEM, 2.6));
            case BUD -> {
                addStem(group, x, height, bend);
                group.getChildren().add(bud(x, top));
            }
            case FLOWER -> {
                addStem(group, x, height, bend);
                group.getChildren().add(flower(x, top));
            }
        }

        double labelsY = GROUND_Y + SOIL_DEPTH + 6;
        Label weekday = dayLabel(WEEKDAYS[date.getDayOfWeek().getValue() - 1], "garden-weekday",
                columnLeft, columnWidth, labelsY);
        Label number = dayLabel(String.valueOf(date.getDayOfMonth()), "garden-date",
                columnLeft, columnWidth, labelsY + 14);
        if (date.equals(today)) {
            weekday.getStyleClass().add("garden-today");
            number.getStyleClass().add("garden-today");
        }
        group.getChildren().addAll(weekday, number);
        return group;
    }

    /** A slightly bent stem, with two leaves once it is tall enough to carry them. */
    private static void addStem(Group group, double x, double height, double bend) {
        CubicCurve stem = new CubicCurve(
                x, GROUND_Y,
                x + 3 * bend, GROUND_Y - height * 0.35,
                x - 3 * bend, GROUND_Y - height * 0.7,
                x, GROUND_Y - height);
        group.getChildren().add(stroke(stem, STEM, 2.6));

        if (height >= 34) {
            double leafY = GROUND_Y - height * 0.42;
            group.getChildren().addAll(leaf(x, leafY, -1), leaf(x, leafY, 1));
        }
    }

    /** One main root, plus a side root each way once it is deep enough. */
    private static void addRoots(Group group, double x, double depth, double bend) {
        CubicCurve root = new CubicCurve(
                x, GROUND_Y,
                x - 2 * bend, GROUND_Y + depth * 0.35,
                x + 2 * bend, GROUND_Y + depth * 0.7,
                x, GROUND_Y + depth);
        group.getChildren().add(stroke(root, ROOT, 2.2));

        if (depth >= 30) {
            group.getChildren().addAll(
                    sideRoot(x, GROUND_Y + depth * 0.32, -bend),
                    sideRoot(x, GROUND_Y + depth * 0.58, bend));
        }
    }

    private static Node sideRoot(double x, double y, double side) {
        return stroke(new QuadCurve(x, y, x + 6 * side, y + 4, x + 10 * side, y + 16), ROOT, 1.5);
    }

    /** A leaf drawn pointing right from the stem, mirrored for the left side. */
    private static Node leaf(double x, double y, double side) {
        SVGPath leaf = new SVGPath();
        leaf.setContent("M0 0 Q5 -7 13 -6 Q8 1 0 0 Z");
        leaf.setFill(LEAF);
        leaf.setStroke(STEM);
        leaf.setStrokeWidth(1);
        leaf.getTransforms().addAll(new Translate(x, y), new Scale(side, 1));
        return leaf;
    }

    /** Five petals around a filled centre, the centre on the top of the stem. */
    private static Group flower(double x, double y) {
        Group flower = new Group();
        for (int k = 0; k < 5; k++) {
            double angle = Math.toRadians(-90 + k * 72);
            Circle petal = new Circle(x + 5.6 * Math.cos(angle), y + 5.6 * Math.sin(angle), 4.4);
            petal.setFill(PETAL);
            petal.setStroke(STEM);
            petal.setStrokeWidth(1);
            flower.getChildren().add(petal);
        }
        Circle centre = new Circle(x, y, 3);
        centre.setFill(STEM);
        flower.getChildren().add(centre);
        return flower;
    }

    /** Sits just above the top of the stem. */
    private static Node bud(double x, double y) {
        Ellipse bud = new Ellipse(x, y - 4, 3.4, 5);
        bud.setFill(Color.WHITE);
        bud.setStroke(STEM);
        bud.setStrokeWidth(1.4);
        return bud;
    }

    /** Always the same size, so a day with nothing never looks like a tiny plant. */
    private static Node seed(double x, double y) {
        Circle seed = new Circle(x, y, 5);
        seed.setFill(Color.WHITE);
        seed.setStroke(SEED);
        seed.setStrokeWidth(1.2);
        seed.getStrokeDashArray().addAll(2.0, 2.0);
        return seed;
    }

    private static Node goalLine(double y, Color colour, double opacity) {
        Line line = line(LEFT, y, RIGHT, y, colour, 1);
        line.getStrokeDashArray().addAll(3.0, 4.0);
        line.setOpacity(opacity);
        return line;
    }

    private static Line line(double x1, double y1, double x2, double y2, Color colour, double width) {
        Line line = new Line(x1, y1, x2, y2);
        stroke(line, colour, width);
        line.setMouseTransparent(true);
        return line;
    }

    private static <T extends Shape> T stroke(T shape, Color colour, double width) {
        shape.setFill(null);
        shape.setStroke(colour);
        shape.setStrokeWidth(width);
        shape.setStrokeLineCap(StrokeLineCap.ROUND);
        return shape;
    }

    /** A label in the left gutter, centred on {@code y}. */
    private static Label gutterLabel(String text, String styleClass, double y, Pos alignment) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setPrefWidth(LEFT - 8);
        label.setAlignment(alignment);
        label.setLayoutY(y - 7);
        return label;
    }

    private static Label dayLabel(String text, String styleClass, double x, double width, double y) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefWidth(width);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private static HBox legendItem(Node icon, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("legend-label");
        HBox item = new HBox(6, icon, label);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    /** Centred in the space above the soil, clear of the seeds. */
    private VBox emptyMessage() {
        Label title = new Label("Nothing planted yet");
        title.getStyleClass().add("garden-empty-title");
        Label body = new Label("Use the app today to plant your first stem.");
        body.getStyleClass().add("garden-empty-body");

        VBox message = new VBox(8, title, body);
        message.setAlignment(Pos.CENTER);
        message.setLayoutX(LEFT);
        message.setLayoutY(34);
        message.setPrefWidth(RIGHT - LEFT);
        // Clicks on the empty space around the message still reach the columns.
        message.setPickOnBounds(false);
        return message;
    }

    /** Left and Right move the selection one day, within the range shown. */
    private void onKey(KeyEvent event) {
        if (selected == null) {
            return;
        }
        LocalDate next;
        if (event.getCode() == KeyCode.LEFT) {
            next = selected.minusDays(1);
        } else if (event.getCode() == KeyCode.RIGHT) {
            next = selected.plusDays(1);
        } else {
            return;
        }
        // Consumed either way, so the arrow keys do not move focus off the garden.
        event.consume();
        if (!next.isBefore(first) && !next.isAfter(last)) {
            onPick.accept(next);
        }
    }
}
