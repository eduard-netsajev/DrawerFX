package drawer;

import drawer.action.Action;
import drawer.box.ColorSlidersBox;
import drawer.buffer.ActionBuffer;
import drawer.buffer.ActionBufferImpl;
import drawer.button.ClearButton;
import drawer.button.SaveButton;
import drawer.button.ShapeToggleButton;
import drawer.mode.DirectorMode;
import drawer.mode.DrawerMode;
import drawer.mode.UsageMode;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * Drawing application. Uses Java 8 and JavaFX features.
 * Allows for drawing strokes or rectangular shapes
 * of different size and color.
 * Different actions on the drawn shapes are supported:
 * 1. Moving shapes
 * 2. Erasing shapes
 *
 * Actions can be undone or redone using Undo and Redo
 * buttons or key ESCAPE and SPACE.
 */
public class DrawerFX extends Application implements DrawerApplication {

    /**
     * Main pane for drawing on it.
     */
    private Pane canvas = new Pane();

    /**
     * Boolean property defining a need of filling the shapes with paint.
     */
    private BooleanProperty fill;

    /**
     * Sample line under the controls to show the user the stroke settings.
     */
    private Line sampleLine;

    private static final Double DEFAULT_STROKE_WIDTH = 3.0;

    private static final Double MAX_STROKE_WIDTH = 30.0;

    private static final Double MIN_STROKE_WIDTH = 1.0;

    private static final int SCENE_WIDTH = 1200;

    private static final int SCENE_HEIGHT = 1000;

    private ActionBuffer buffer = new ActionBufferImpl();

    private UsageMode director = new DirectorMode(this);

    private UsageMode drawer = new DrawerMode(this);

    private UsageMode currentMode = drawer;

    private ShapeMode currentShapeMode = ShapeMode.STROKE;

    private MouseEventDelegator handleDelegator = new MouseEventDelegator(this);

    @Override
    public void start(Stage primaryStage) {
        Scene scene = createMainScene();
        setSceneKeyboardListeners(scene);
        setCanvasMouseEventHandlers();
        setAndShowStage(primaryStage, scene);
        setApplicationModes();
    }

    private Scene createMainScene() {
        final BorderPane root = createRootPane();
        return new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private BorderPane createRootPane() {
        BorderPane root = new BorderPane();
        VBox topPane = createApplicationHeadRegion();
        setupCanvas();
        root.setCenter(canvas);
        root.setTop(topPane);
        return root;
    }

    private void setupCanvas() {
        canvas.setCursor(Cursor.CROSSHAIR);
    }

    private void setAndShowStage(Stage primaryStage, Scene scene) {
        primaryStage.setTitle("DrawerFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createApplicationHeadRegion() {
        ToggleGroup shapeModeToggles = new ToggleGroup();
        addShapeToggleListener(shapeModeToggles);

        HBox toolBox = createToolBox(shapeModeToggles);

        StackPane sampleLineContainer = createSampleLineContainer();

        return createTopSection(toolBox, sampleLineContainer);
    }

    private void setApplicationModes() {
        director = new DirectorMode(this);
        drawer = new DrawerMode(this);
    }

    private void setSceneKeyboardListeners(Scene scene) {
        scene.setOnKeyPressed(ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                undo();
            } else if (ke.getCode() == KeyCode.SPACE) {
                redo();
            } else if (ke.getCode() == KeyCode.CONTROL) {
                currentMode = director;
            }
        });
        scene.setOnKeyReleased(ke -> {
            if (ke.getCode() == KeyCode.CONTROL) {
                currentMode = drawer;
            }
        });
    }

    private HBox createToolBox(ToggleGroup modeChoice) {
        VBox toggleBoxFirst = createFirstToolBox(modeChoice);
        VBox toggleBoxSecond = createSecondToolBox(modeChoice);

        VBox bufferBox = createBufferBox();
        VBox utilityBox = createUtilityBox();

        ColorSlidersBox colorSlidersBox = new ColorSlidersBox();
        sampleLine.strokeProperty().bind(colorSlidersBox.getSlidersColorBinding());

        // Put all controls in one HBox
        HBox toolBox = new HBox(75);
        toolBox.setAlignment(Pos.TOP_CENTER);
        toolBox.getChildren().addAll(bufferBox, toggleBoxFirst, toggleBoxSecond,
                utilityBox, colorSlidersBox);
        return toolBox;
    }

    private StackPane createSampleLineContainer() {
        StackPane stackpane = new StackPane();
        stackpane.setPrefHeight(MAX_STROKE_WIDTH);
        stackpane.getChildren().add(sampleLine);
        return stackpane;
    }

    private VBox createUtilityBox() {
        Slider strokeSlider = new Slider(MIN_STROKE_WIDTH, MAX_STROKE_WIDTH, DEFAULT_STROKE_WIDTH);
        createSampleLine(strokeSlider);

        CheckBox fillBox = new CheckBox("Fill");
        fill = fillBox.selectedProperty();

        Button clearButton = new ClearButton(canvas);

        return new UtilityBox(clearButton, strokeSlider, fillBox);
    }

    private void createSampleLine(Slider strokeSlider) {
        sampleLine = new Line(0, 0, 150, 0);
        sampleLine.strokeWidthProperty().bind(strokeSlider.valueProperty());
    }

    private VBox createBufferBox() {
        VBox bufferBox = new VBox(10);
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(event -> undo());

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(event -> redo());

        Button saveButton = new SaveButton(canvas);

        bufferBox.getChildren().addAll(undoButton, redoButton, saveButton);
        return bufferBox;
    }

    private VBox createTopSection(HBox toolBox, StackPane stackpane) {
        VBox topPane = new VBox(20);
        topPane.setPrefWidth(SCENE_WIDTH - 20);
        topPane.setLayoutY(20);
        topPane.setLayoutX(10);
        topPane.getChildren().addAll(toolBox, stackpane);
        return topPane;
    }

    private VBox createFirstToolBox(ToggleGroup modeChoice) {
        VBox toggleBox = new VBox(10);
        ToggleButton toggleButtonStroke = new ShapeToggleButton("Stroke", ShapeMode.STROKE);
        toggleButtonStroke.setSelected(true);
        toggleButtonStroke.setToggleGroup(modeChoice);

        ToggleButton toggleButtonRectangular = new ShapeToggleButton("Rectangle", ShapeMode.RECTANGULAR);
        toggleButtonRectangular.setToggleGroup(modeChoice);

        ToggleButton toggleButtonEllipse = new ShapeToggleButton("Ellipse", ShapeMode.ELLIPSE);
        toggleButtonEllipse.setToggleGroup(modeChoice);
        toggleBox.getChildren().addAll(toggleButtonStroke, toggleButtonRectangular, toggleButtonEllipse);
        return toggleBox;
    }

    private VBox createSecondToolBox(ToggleGroup modeChoice) {
        VBox toggleBox2 = new VBox(10);

        ToggleButton toggleButtonLine = new ShapeToggleButton("Line", ShapeMode.LINE);
        toggleButtonLine.setToggleGroup(modeChoice);

        ToggleButton toggleButtonSquare = new ShapeToggleButton("Square", ShapeMode.SQUARE);
        toggleButtonSquare.setToggleGroup(modeChoice);

        ToggleButton toggleButtonCircle = new ShapeToggleButton("Circle", ShapeMode.CIRCLE);
        toggleButtonCircle.setToggleGroup(modeChoice);

        toggleBox2.getChildren().addAll(toggleButtonLine, toggleButtonSquare, toggleButtonCircle);
        return toggleBox2;
    }

    private void addShapeToggleListener(ToggleGroup modeChoice) {
        modeChoice.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                currentShapeMode = newValue instanceof ShapeToggleButton ?
                        ((ShapeToggleButton) newValue).getShapeMode() : ShapeMode.UNKNOWN;
            }
        });
    }
    private void setCanvasMouseEventHandlers() {
        canvas.setOnMouseClicked(handleDelegator::handleClick);
        canvas.setOnMousePressed(handleDelegator::handlePress);
        canvas.setOnMouseReleased(handleDelegator::handleRelease);
        canvas.setOnMouseDragged(handleDelegator::handleDrag);
    }

    @Override
    public void registerShapeHandlers(Shape shape) {
        shape.setOnMousePressed(handleDelegator::handlePress);
        shape.setOnMouseDragged(handleDelegator::handleDrag);
        shape.setOnMouseEntered(handleDelegator::handleEnter);
        shape.setOnMouseExited(handleDelegator::handleExit);
        shape.setOnMouseClicked(handleDelegator::handleClick);
    }

    @Override
    public Line getSampleLine() {
        return sampleLine;
    }
    private void redo() {
        Action action = buffer.getNext();
        action.redo();
    }
    private void undo() {
        Action action = buffer.getPrevious();
        action.undo();
    }
    @Override
    public Pane getCanvas() {
        return canvas;
    }
    @Override
    public ObservableBooleanValue getFillShapeProperty() {
        return fill;
    }
    @Override
    public UsageMode getUsageMode() {
        return currentMode;
    }
    @Override
    public ActionBuffer getBuffer() {
        return buffer;
    }
    @Override
    public ShapeMode getShapeMode() {
        return currentShapeMode;
    }
}