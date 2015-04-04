package drawer;

import drawer.actions.Action;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    private Pane canvas;

    /**
     * Checkbox defining a need of filling the shapes with paint.
     */
    private CheckBox fillBox;

    /**
     * Sample line under the controls to show the user the stroke settings.
     */
    private Line sampleLine;

    /**
     * Rectangle drawing start point X and Y coordinates.
     */
    private double rsX, rsY;

    /**
     * Starting width of a stroke.
     */
    private static final Double DEFAULTSTROKE = 3.0;

    /**
     * Maximum width of a stroke.
     */
    private static final Double MAXSTROKE = 30.0;

    /**
     * Minimum width of a stroke.
     */
    private static final Double MINSTROKE = 1.0;

    /**
     * Starting scene window width.
     */
    private static final int SCENE_WIDTH = 1200;

    /**
     * Starting scene window height.
     */
    private static final int SCENE_HEIGHT = 1000;

    /**
     * Action objects collection - buffer.
     */
    private EditHistoryBuffer buffer = new EditHistoryBuffer();

    private UsageMode currentMode;

    private ToggledShape currentShapeMode = ToggledShape.STROKE;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("DrawerFX");
        final BorderPane root = new BorderPane();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        canvas = new Pane();
        canvas.setCursor(Cursor.CROSSHAIR);
        EventHandler<KeyEvent> filter = ke -> {
            if (ke.getCode() == KeyCode.SPACE) {
                ke.consume();
                redo();
            }
        };
        scene.addEventFilter(KeyEvent.KEY_PRESSED, filter);

        // Toggle Buttons group for switching between drawing different shapes.
        ToggleGroup modeChoice = new ToggleGroup();

        ToggleButton toggleButtonStroke = new ToggleButton("Stroke");
        toggleButtonStroke.setSelected(true);
        toggleButtonStroke.setToggleGroup(modeChoice);

        ToggleButton toggleButtonRectangular = new ToggleButton("Rectangle");
        toggleButtonRectangular.setToggleGroup(modeChoice);

        VBox toggleBox2 = new VBox(10);

        ToggleButton toggleButtonCircle = new ToggleButton("Circle");
        toggleButtonCircle.setToggleGroup(modeChoice);

        ToggleButton toggleButtonLine = new ToggleButton("Line");
        toggleButtonLine.setToggleGroup(modeChoice);

        ToggleButton toggleButtonSquare = new ToggleButton("Square");
        toggleButtonSquare.setToggleGroup(modeChoice);

        ToggleButton toggleButtonEllipse = new ToggleButton("Ellipse");
        toggleButtonEllipse.setToggleGroup(modeChoice);

        modeChoice.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == toggleButtonStroke)
                    currentShapeMode = ToggledShape.STROKE;
                else if (newValue == toggleButtonRectangular)
                    currentShapeMode = ToggledShape.RECTANGULAR;
                else if (newValue == toggleButtonCircle)
                    currentShapeMode = ToggledShape.CIRCLE;
                else if (newValue == toggleButtonLine)
                    currentShapeMode = ToggledShape.LINE;
                else if (newValue == toggleButtonSquare)
                    currentShapeMode = ToggledShape.SQUARE;
                else if (newValue == toggleButtonEllipse)
                    currentShapeMode = ToggledShape.ELLIPSE;
            }
        });

        // VBox for the toggle buttons
        VBox toggleBox = new VBox(10);
        toggleBox.getChildren().addAll(toggleButtonStroke, toggleButtonRectangular, toggleButtonEllipse);
        toggleBox2.getChildren().addAll(toggleButtonLine, toggleButtonSquare, toggleButtonCircle);
        // VBox for the buffer buttons
        VBox bufferBox = new VBox(10);
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(event -> undo());

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(event -> redo());

        Button saveButton = new SaveButton(canvas);

        bufferBox.getChildren().addAll(undoButton, redoButton, saveButton);

        Slider strokeSlider = new Slider(MINSTROKE, MAXSTROKE, DEFAULTSTROKE);

        fillBox = new CheckBox("Fill");
        Button clearButton = new ClearButton(canvas);
        VBox utilBox = new UtilityBox(clearButton, strokeSlider, fillBox);

        ColorSlidersBox colorSlidersBox = new ColorSlidersBox();

        // Put all controls in one HBox
        HBox toolBox = new HBox(75);
        toolBox.setAlignment(Pos.TOP_CENTER);
        toolBox.getChildren().addAll(bufferBox, toggleBox, toggleBox2,
                utilBox, colorSlidersBox);

        // Build the sample line and its layout container
        sampleLine = new Line(0, 0, 150, 0);
        sampleLine.strokeWidthProperty().bind(strokeSlider.valueProperty());
        StackPane stackpane = new StackPane();
        stackpane.setPrefHeight(MAXSTROKE);
        stackpane.getChildren().add(sampleLine);
        // Bind to the Paint Binding object
        sampleLine.strokeProperty().bind(colorSlidersBox.getSlidersColorBinding());

        // Build the VBox container for the toolBox and sampleline
        VBox vb = new VBox(20);
        vb.setPrefWidth(scene.getWidth() - 20);
        vb.setLayoutY(20);
        vb.setLayoutX(10);
        vb.getChildren().addAll(toolBox, stackpane);
        root.setTop(vb);
        //root.getChildren().addAll(shapes);
        root.setCenter(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();

        UsageMode director = new DirectorMode(this);
        UsageMode drawer = new DrawerMode(this);
        currentMode = drawer;

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

        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handleClick(event);
            }
        });
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handlePress(event);
            }
        });
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handleRelease(event);
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handleDrag(event);
            }
        });

    }

    @Override
    public void registerShapeHandlers(Shape shape) {
        shape.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handlePress(event);
            }
        });
        shape.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handleDrag(event);
            }
        });
        shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handleEnter(event);
            }
        });
        shape.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handleExit(event);
            }
        });
        shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentMode.handleClick(event);
            }
        });
    }

    @Override
    public Line getSampleLine() {
        return sampleLine;
    }

    private void redo() {
        Action action = buffer.getNextAction();
        action.redo();
    }

    private void undo() {
        Action action = buffer.getPreviousAction();
        action.undo();
    }

    @Override
    public Pane getCanvas() {
        return canvas;
    }

    @Override
    public BooleanProperty getFillShapeProperty() {
        return fillBox.selectedProperty();
    }

    @Override
    public UsageMode getUsageMode() {
        return null;
    }


    @Override
    public EditHistoryBuffer getBuffer() {
        return buffer;
    }

    @Override
    public ToggledShape getShapeMode() {
        return currentShapeMode;
    }
}