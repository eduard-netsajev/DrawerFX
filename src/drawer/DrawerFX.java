package drawer;

import drawer.actions.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
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
public class DrawerFX extends Application {

    /**
     * Shadow effect for highlighting shapes.
     */
    private final DropShadow shadow = new DropShadow(15, Color.BLACK);

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
     * Boolean value displaying whether anything is being currently drawn.
     */
    private boolean drawingShape = false;

    /**
     * Boolean value for displaying director or drawer modes.
     */
    private boolean dirMode = false;

    /**
     * Toggle Buttons group for switching between drawing different shapes.
     */
    private ToggleGroup modeChoice;

    /**
     * Toggle Buttons for drawing Stroke and Rectangle shapes.
     */
    private ToggleButton toggleButtonStroke, toggleRectangular,
            toggleButtonCircle, toggleButtonLine,
            toggleButtonEllipse, toggleButtonSquare;

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

    /**
     * Temporary object for holding Shape object while drawing.
     */
    private Shape shape;

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

        modeChoice = new ToggleGroup();
        toggleButtonStroke = new ToggleButton("Stroke");
        toggleButtonStroke.setSelected(true);
        toggleRectangular = new ToggleButton("Rectangle");
        toggleButtonStroke.setToggleGroup(modeChoice);
        toggleRectangular.setToggleGroup(modeChoice);

        VBox toggleBox2 = new VBox(10);

        toggleButtonCircle = new ToggleButton("Circle");
        toggleButtonCircle.setToggleGroup(modeChoice);

        toggleButtonLine = new ToggleButton("Line");
        toggleButtonLine.setToggleGroup(modeChoice);

        toggleButtonSquare = new ToggleButton("Square");
        toggleButtonSquare.setToggleGroup(modeChoice);

        toggleButtonEllipse = new ToggleButton("Ellipse");
        toggleButtonEllipse.setToggleGroup(modeChoice);

        // VBox for the toggle buttons
        VBox toggleBox = new VBox(10);
        toggleBox.getChildren().addAll(toggleButtonStroke, toggleRectangular, toggleButtonEllipse);
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

        canvas.setOnMouseClicked(clickHandler);
        canvas.setOnMousePressed(pressHandler);
        canvas.setOnMouseReleased(releaseHandler);
        canvas.setOnMouseDragged(drugHandler);

        scene.setOnKeyPressed(ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                undo();
            } else if (ke.getCode() == KeyCode.SPACE) {
                redo();
            } else if (ke.getCode() == KeyCode.CONTROL) {
                dirMode = true;
            }
        });
        scene.setOnKeyReleased(ke -> {
            if (ke.getCode() == KeyCode.CONTROL) {
                dirMode = false;
            }
        });
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
    }

    /**
     * Mouse event handler for MouseEvent.MOUSE_CLICKED events.
     */
    EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
            if (dirMode) {
                if (me.getButton() == MouseButton.SECONDARY
                        && me.getSource() instanceof Shape) {
                    Shape shape = (Shape) me.getSource();
                    canvas.getChildren().remove(shape);
                    buffer.addAction(new EraseAction(canvas, shape));
                }
            } else {
                if (me.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                if (drawingShape) {
                    drawingShape = false;
                    return;
                }

                double a = sampleLine.getStrokeWidth() / 2.0;
                Rectangle point = new Rectangle(me.getX() - a,
                        me.getY() - a, a * 2.0, a * 2.0);
                buffer.addAction(new DrawAction(canvas, point));
                point.setFill(sampleLine.getStroke());
                setMouseEventHandlers(point);
                canvas.getChildren().add(point);
            }
        }
    };

    /**
     * Mouse event handler for MouseEvent.MOUSE_DRAGGED events.
     */
    EventHandler<MouseEvent> drugHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
            if (me.getButton() != MouseButton.PRIMARY) {
                return;
            }
            if (dirMode) {
                if (me.getSource() instanceof Shape) {
                    Action previousAction = buffer.peekPreviousAction();
                    if (previousAction instanceof MoveAction) {
                        MoveAction moveAction = (MoveAction) previousAction;

                        Point2D originalMousePoint = moveAction.getOriginalMousePoint();

                        double offsetX = me.getX() - originalMousePoint.getX();
                        double offsetY = me.getY() - originalMousePoint.getY();

                        Shape shape = ((Shape) me.getSource());
                        double newLayoutX = shape.getLayoutX() + offsetX;
                        double newLayoutY = shape.getLayoutY() + offsetY;

                        shape.setLayoutX(newLayoutX);
                        shape.setLayoutY(newLayoutY);

                        Point2D newPoint = new Point2D(newLayoutX, newLayoutY);
                        moveAction.setNewLayoutPoint(newPoint);
                    }
                }
            } else {
                drawingShape = true;
                if (modeChoice.getSelectedToggle() == toggleButtonStroke && shape != null) {
                    LineTo lineTo = new LineTo(me.getX(), me.getY());
                    ((Path) shape).getElements().add(lineTo);
                } else if (modeChoice.getSelectedToggle() == toggleRectangular
                        && shape != null) {
                    double meX = me.getX();
                    double meY = me.getY();

                    Rectangle rect = ((Rectangle) shape);

                    if (rsX < meX) {
                        if (rsY < meY) {

                            rect.setX(rsX);
                            rect.setY(rsY);
                            rect.setWidth(meX - rsX);
                            rect.setHeight(meY - rsY);
                        } else {
                            rect.setX(rsX);
                            rect.setY(meY);
                            rect.setWidth(meX - rsX);
                            rect.setHeight(rsY - meY);
                        }
                    } else {
                        if (rsY < meY) {

                            rect.setX(meX);
                            rect.setY(rsY);
                            rect.setWidth(rsX - meX);
                            rect.setHeight(meY - rsY);
                        } else {
                            rect.setX(meX);
                            rect.setY(meY);
                            rect.setWidth(rsX - meX);
                            rect.setHeight(rsY - meY);
                        }
                    }

                } else if (modeChoice.getSelectedToggle() == toggleButtonCircle && shape != null) {

                    double meX = me.getX();
                    double meY = me.getY();

                    Circle circle = ((Circle) shape);

                    if (rsX < meX) {
                        circle.setCenterX(rsX + (meX - rsX) / 2);
                        if (rsY < meY) {
                            circle.setCenterY(rsY + (meY - rsY) / 2);
                            circle.setRadius(Math.max(meX - circle.getCenterX(), meY - circle.getCenterY()));
                        } else {
                            circle.setCenterY(meY + (rsY - meY) / 2);
                            circle.setRadius(Math.max(meX - circle.getCenterX(), rsY - circle.getCenterY()));
                        }
                    } else {
                        circle.setCenterX(meX + (rsX - meX) / 2);
                        if (rsY < meY) {
                            circle.setCenterY(rsY + (meY - rsY) / 2);
                            circle.setRadius(Math.max(rsX - circle.getCenterX(), meY - circle.getCenterY()));
                        } else {
                            circle.setCenterY(meY + (rsY - meY) / 2);
                            circle.setRadius(Math.max(rsX - circle.getCenterX(), rsY - circle.getCenterY()));
                        }
                    }

                } else if (modeChoice.getSelectedToggle() == toggleButtonLine && shape != null) {
                    double meX = me.getX();
                    double meY = me.getY();

                    Line line = ((Line) shape);
                    line.setStartX(rsX);
                    line.setStartY(rsY);
                    line.setEndX(meX);
                    line.setEndY(meY);
                } else if (modeChoice.getSelectedToggle() == toggleButtonEllipse && shape != null) {

                    double meX = me.getX();
                    double meY = me.getY();

                    Ellipse ellipse = ((Ellipse) shape);

                    if (rsX < meX) {
                        ellipse.setCenterX(rsX + (meX - rsX) / 2);
                        ellipse.setRadiusX(meX - ellipse.getCenterX());
                        if (rsY < meY) {
                            ellipse.setCenterY(rsY + (meY - rsY) / 2);
                            ellipse.setRadiusY(meY - ellipse.getCenterY());
                        } else {
                            ellipse.setCenterY(meY + (rsY - meY) / 2);
                            ellipse.setRadiusY(rsY - ellipse.getCenterY());
                        }
                    } else {
                        ellipse.setCenterX(meX + (rsX - meX) / 2);
                        ellipse.setRadiusX(rsX - ellipse.getCenterX());
                        if (rsY < meY) {
                            ellipse.setCenterY(rsY + (meY - rsY) / 2);
                            ellipse.setRadiusY(meY - ellipse.getCenterY());
                        } else {
                            ellipse.setCenterY(meY + (rsY - meY) / 2);
                            ellipse.setRadiusY(rsY - ellipse.getCenterY());
                        }
                    }
                } else if (modeChoice.getSelectedToggle() == toggleButtonSquare && shape != null) {
                    double meX = me.getX();
                    double meY = me.getY();

                    Rectangle square = ((Rectangle) shape);

                    if (rsX < meX) {
                        if (rsY < meY) {
                            // lower right
                            square.setX(rsX);
                            square.setY(rsY);
                            square.setWidth(meX - rsX);
                            square.setHeight(meX - rsX);
                        } else {
                            // upper right
                            square.setX(rsX);
                            square.setY(meY);
                            square.setWidth(rsY - meY);
                            square.setHeight(rsY - meY);
                        }
                    } else {
                        if (rsY < meY) {
                            // lower left
                            square.setX(meX);
                            square.setY(rsY);
                            square.setWidth(rsX - meX);
                            square.setHeight(rsX - meX);
                        } else {
                            // upper right
                            square.setX(meX);
                            square.setY(rsY - rsX + meX);
                            square.setWidth(rsX - meX);
                            square.setHeight(rsX - meX);
                        }
                    }
                }
            }
        }
    };

    /**
     * Mouse event handler for MouseEvent.MOUSE_PRESSED events.
     */
    EventHandler<MouseEvent> pressHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
            if (me.getButton() != MouseButton.PRIMARY) {
                return;
            }
            if (dirMode) {

                if (me.getSource() instanceof Shape) {

                    Shape shape = ((Shape) me.getSource());
                    MoveAction moveAction = new MoveAction(shape);

                    Point2D originalPoint = new Point2D(me.getX(), me.getY());
                    moveAction.setOriginalMousePoint(originalPoint);

                    Point2D oldLayoutPoint = new Point2D(shape.getLayoutX(), shape.getLayoutY());
                    moveAction.setOldLayoutPoint(oldLayoutPoint);

                    buffer.addAction(moveAction);
                }
            } else {

                if (modeChoice.getSelectedToggle() == toggleButtonStroke) {

                    Path path = new Path();

                    buffer.addAction(new DrawAction(canvas, path));

                    path.setStrokeWidth(sampleLine.getStrokeWidth());
                    path.setStroke(sampleLine.getStroke());

                    setMouseEventHandlers(path);

                    canvas.getChildren().add(path);
                    path.getElements().add(
                            new MoveTo(me.getX(), me.getY()));

                    shape = path;

                } else if (modeChoice.getSelectedToggle() == toggleRectangular) {

                    // Rectangle-Start
                    rsX = me.getX();
                    rsY = me.getY();

                    Rectangle rect = new Rectangle(rsX, rsY, 0, 0);

                    if (fillBox.isSelected()) {
                        rect.setFill(sampleLine.getStroke());
                    } else {
                        rect.setFill(Color.TRANSPARENT);
                        rect.setStroke(sampleLine.getStroke());
                        rect.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(rect);

                    buffer.addAction(new DrawAction(canvas, rect));

                    setMouseEventHandlers(rect);
                    shape = rect;
                } else if (modeChoice.getSelectedToggle() == toggleButtonCircle) {

                    //Circle drawing
                    rsX = me.getX();
                    rsY = me.getY();

                    Circle circle;

                    if (fillBox.isSelected()) {
                        circle = new Circle(0, sampleLine.getStroke());
                    } else {
                        circle = new Circle(0, Color.TRANSPARENT);
                        circle.setStroke(sampleLine.getStroke());
                        circle.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(circle);
                    buffer.addAction(new DrawAction(canvas, circle));

                    setMouseEventHandlers(circle);
                    shape = circle;

                } else if (modeChoice.getSelectedToggle() == toggleButtonLine) {

                    //Line drawing
                    rsX = me.getX();
                    rsY = me.getY();

                    Line line = new Line(rsX, rsY, rsX, rsY);
                    line.setStrokeWidth(sampleLine.getStrokeWidth());
                    line.setStroke(sampleLine.getStroke());
                    canvas.getChildren().add(line);

                    buffer.addAction(new DrawAction(canvas, line));

                    setMouseEventHandlers(line);
                    shape = line;

                } else if (modeChoice.getSelectedToggle() == toggleButtonEllipse) {

                    //Ellipse drawing
                    rsX = me.getX();
                    rsY = me.getY();
                    Ellipse ellipse = new Ellipse(0, 0);

                    if (fillBox.isSelected()) {
                        ellipse.setFill(sampleLine.getStroke());
                    } else {
                        ellipse.setFill(Color.TRANSPARENT);
                        ellipse.setStroke(sampleLine.getStroke());
                        ellipse.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(ellipse);
                    buffer.addAction(new DrawAction(canvas, ellipse));

                    setMouseEventHandlers(ellipse);
                    shape = ellipse;

                } else if (modeChoice.getSelectedToggle() == toggleButtonSquare) {

                    //Ellipse drawing
                    rsX = me.getX();
                    rsY = me.getY();
                    Rectangle square = new Rectangle(0, 0);

                    if (fillBox.isSelected()) {
                        square.setFill(sampleLine.getStroke());
                    } else {
                        square.setFill(Color.TRANSPARENT);
                        square.setStroke(sampleLine.getStroke());
                        square.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(square);

                    buffer.addAction(new DrawAction(canvas, square));

                    setMouseEventHandlers(square);
                    shape = square;
                }
            }
        }
    };

    private void setMouseEventHandlers(Shape shape) {
        shape.setOnMousePressed(pressHandler);
        shape.setOnMouseDragged(drugHandler);
        shape.setOnMouseEntered(enterHandler);
        shape.setOnMouseExited(exitHandler);
        shape.setOnMouseClicked(clickHandler);
    }

    /**
     * Mouse event handler for MouseEvent.MOUSE_RELEASED events.
     */
    EventHandler<MouseEvent> releaseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            shape = null;
        }
    };

    /**
     * Mouse event handler for MouseEvent.MouseEvent.MOUSE_ENTERED events.
     */
    EventHandler<MouseEvent> enterHandler = me -> {
        if (dirMode && me.getSource() instanceof Shape) {
            ((Shape) me.getSource()).setEffect(shadow);
        }
    };

    /**
     * Mouse event handler for MouseEvent.MouseEvent.MOUSE_EXITED events.
     */
    EventHandler<MouseEvent> exitHandler = me -> {
        if (me.getSource() instanceof Shape)
            ((Shape) me.getSource()).setEffect(null);
    };

    private void redo() {
        Action action = buffer.getNextAction();
        action.redo();
    }

    private void undo() {
        Action action = buffer.getPreviousAction();
        action.undo();
    }

}