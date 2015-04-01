import javafx.application.Application;
import javafx.beans.binding.ObjectBinding;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;

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

    private static final int MILLISECS_IN_SEC = 1000;

    /**
     * Shadow effect for highlighting shapes.
     */
    private final DropShadow shadow = new DropShadow(15, Color.BLACK);

    /**
     * Main pane for drawing on it.
     */
    private Pane canvas;

    /**
     * Temporary object for holding Path object while drawing.
     */
    private Path path;

    /**
     * Temporary object for holding Rectangle object while drawing.
     */
    private Rectangle rect;

    private Circle circ;

    private Line line;

    private Ellipse ellipse;

    private Rectangle square;

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
     * Starting value of RED pigment in color picker.
     */
    private static final Integer DEFAULTRED = 0;

    /**
     * Starting value of GREEN pigment in color picker.
     */
    private static final Integer DEFAULTGREEN = 0;

    /**
     * Starting value of BLUE pigment in color picker.
     */
    private static final Integer DEFAULTBLUE = 255;

    /**
     * Maximum value for any color pigment in color picker.
     */
    private static final Integer MAXRGB = 255;

    /**
     * Minimum value for any color pigment in color picker.
     */
    private static final Integer MINRGB = 0;

    /**
     * Boolean value displaying whether anything is being currently drawn.
     */
    private boolean drawingShape = false;

    /**
     * Moving action cursor starting X and Y coordinates.
     */
    private double orgSceneX, orgSceneY;

    /**
     * Moving action shape starting X and Y coordinates.
     */
    private double orgTranslateX, orgTranslateY;

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
     * Integer value of 10.
     */
    private static final int TEN = 10;

    /**
     * Integer value of 5.
     */
    private static final int FIVE = 5;

    /**
     * Integer value of 77.
     */
    private static final int SEVEN_SEVEN = 77;

    /**
     * Action objects array - buffer.
     */
    private Action[] buffer = new Action[TEN * TEN * TEN * TEN];

    /**
     * Integer value used to navigate in buffer.
     */
    private int maxAction = 0;

    /**
     * Integer value used to navigate in buffer.
     */
    private int currentAction = 0;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Kimp");
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

        VBox toggleBox2 = new VBox(TEN);

        toggleButtonCircle = new ToggleButton("Circle");
        toggleButtonCircle.setToggleGroup(modeChoice);

        toggleButtonLine = new ToggleButton("Line");
        toggleButtonLine.setToggleGroup(modeChoice);

        toggleButtonSquare = new ToggleButton("Square");
        toggleButtonSquare.setToggleGroup(modeChoice);

        toggleButtonEllipse = new ToggleButton("Ellipse");
        toggleButtonEllipse.setToggleGroup(modeChoice);

        // VBox for the toggle buttons
        VBox toggleBox = new VBox(TEN);
        toggleBox.getChildren().addAll(toggleButtonStroke, toggleRectangular, toggleButtonEllipse);
        toggleBox2.getChildren().addAll(toggleButtonLine, toggleButtonSquare, toggleButtonCircle);
        // VBox for the buffer buttons
        VBox bufferBox = new VBox(TEN);
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(event -> undo());

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(event -> redo());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            WritableImage image = canvas.snapshot(snapshotParameters, null);
            File file = new File(String.format("saved_%d.png", System.currentTimeMillis() / MILLISECS_IN_SEC));
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                System.out.println("Saved image at " + file.getAbsolutePath());
            } catch (Exception e) {
                System.out.println("exception");
            }
        });

        bufferBox.getChildren().addAll(undoButton, redoButton, saveButton);

        // Build the slider, label, and button and their VBox layout container 
        Button btnClear = new Button();
        btnClear.setText("Clear");
        btnClear.setOnAction(event ->
                canvas.getChildren().removeAll(canvas.getChildren()));

        Slider strokeSlider = new Slider(MINSTROKE, MAXSTROKE, DEFAULTSTROKE);
        Label labelStroke = new Label("Stroke Width");
        fillBox = new CheckBox("Fill");
        VBox utilBox = new VBox(TEN);
        utilBox.setAlignment(Pos.TOP_CENTER);
        utilBox.getChildren().addAll(btnClear, labelStroke, strokeSlider, fillBox);

        // Build the RGB sliders, labels, and HBox containers
        Slider redSlider = new Slider(MINRGB, MAXRGB, DEFAULTRED);
        Label labelRed = new Label("R");
        HBox rhbox = new HBox(FIVE);
        rhbox.getChildren().addAll(labelRed, redSlider);

        Slider greenSlider = new Slider(MINRGB, MAXRGB, DEFAULTGREEN);
        Label labelGreen = new Label("G");
        HBox ghbox = new HBox(FIVE);
        ghbox.getChildren().addAll(labelGreen, greenSlider);

        Slider blueSlider = new Slider(MINRGB, MAXRGB, DEFAULTBLUE);
        Label labelBlue = new Label("B");
        HBox bhbox = new HBox(FIVE);
        bhbox.getChildren().addAll(labelBlue, blueSlider);

        // Build the VBox container for all the slider containers        
        VBox colorBox = new VBox(TEN);
        colorBox.setAlignment(Pos.TOP_CENTER);
        colorBox.getChildren().addAll(rhbox, ghbox, bhbox);

        // Put all controls in one HBox
        HBox toolBox = new HBox(SEVEN_SEVEN);
        toolBox.setAlignment(Pos.TOP_CENTER);
        toolBox.getChildren().addAll(bufferBox, toggleBox, toggleBox2,
                utilBox, colorBox);

        // Build a Binding object to compute a Paint object from the sliders
        ObjectBinding<Paint> colorBinding = new ObjectBinding<Paint>() {
            {
                super.bind(redSlider.valueProperty(),
                        greenSlider.valueProperty(),
                        blueSlider.valueProperty());
            }
            @Override
            protected Paint computeValue() {
                return Color.rgb(redSlider.valueProperty().intValue(),
                        greenSlider.valueProperty().intValue(),
                        blueSlider.valueProperty().intValue());
            }
        };
        // Build the sample line and its layout container
        sampleLine = new Line(0, 0, SEVEN_SEVEN * 2, 0);
        sampleLine.strokeWidthProperty().bind(strokeSlider.valueProperty());
        StackPane stackpane = new StackPane();
        stackpane.setPrefHeight(MAXSTROKE);
        stackpane.getChildren().add(sampleLine);
        // Bind to the Paint Binding object
        sampleLine.strokeProperty().bind(colorBinding);

        canvas.setOnMouseClicked(clickHandler);
        canvas.setOnMousePressed(pressHandler);
        canvas.setOnMouseReleased(releaseHandler);
        canvas.setOnMouseDragged(drugHandler);

        scene.setOnKeyPressed(ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                if (currentAction > 0) {
                    undo();
                } else {
                    Stage stage = new Stage();
                    stage.setTitle("Exit Confirmation");
                    // Set a scene with a button in the stage
                    VBox pane = new VBox(TEN);
                    Label question = new Label("Are you sure you want to leave"
                            + " the program?");
                    HBox choice = new HBox(TEN);
                    Button yes = new Button("Yes");
                    Button no = new Button("No");
                    no.setOnAction(event -> stage.close());
                    yes.setOnAction(event -> System.exit(0));
                    choice.getChildren().addAll(yes, no);
                    pane.getChildren().addAll(question, choice);
                    BorderPane pp = new BorderPane();
                    pp.setCenter(pane);
                    stage.setScene(new Scene(pp));
                    stage.show();
                }
            } else if (ke.getCode() == KeyCode.CONTROL) {
                dirMode = true;
            } else if (ke.getCode() == KeyCode.SPACE) {
                ke.consume();
                redo();
            }
        });
        scene.setOnKeyReleased(ke -> {
            if (ke.getCode() == KeyCode.CONTROL) {
                dirMode = false;
            }
        });
        // Build the VBox container for the toolBox and sampleline
        VBox vb = new VBox(TEN * 2);
        vb.setPrefWidth(scene.getWidth() - TEN * 2);
        vb.setLayoutY(TEN * 2);
        vb.setLayoutX(TEN);
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
                    buffer[currentAction] = new EraseAction((Shape)
                            me.getSource());
                    canvas.getChildren().remove(me.getSource());
                    currentAction++;
                    maxAction = currentAction;
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
                buffer[currentAction] = new DrawAction(point);
                currentAction++;
                maxAction = currentAction;
                point.setFill(sampleLine.getStroke());
                point.setOnMousePressed(pressHandler);
                point.setOnMouseDragged(drugHandler);
                point.setOnMouseEntered(enterHandler);
                point.setOnMouseExited(exitHandler);
                point.setOnMouseClicked(clickHandler);
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
                double offsetX = me.getSceneX() - orgSceneX;
                double offsetY = me.getSceneY() - orgSceneY;
                double newTranslateX = orgTranslateX + offsetX;
                double newTranslateY = orgTranslateY + offsetY;

                if (me.getSource() instanceof Rectangle) {
                    ((Rectangle) (me.getSource())).setTranslateX(newTranslateX);
                    ((Rectangle) (me.getSource())).setTranslateY(newTranslateY);
                } else if (me.getSource() instanceof Path) {
                    ((Path) (me.getSource())).setTranslateX(newTranslateX);
                    ((Path) (me.getSource())).setTranslateY(newTranslateY);
                } else if (me.getSource() instanceof Circle) {
                    ((Circle) (me.getSource())).setTranslateX(newTranslateX);
                    ((Circle) (me.getSource())).setTranslateY(newTranslateY);
                } else if (me.getSource() instanceof Line) {
                    ((Line) (me.getSource())).setTranslateX(newTranslateX);
                    ((Line) (me.getSource())).setTranslateY(newTranslateY);
                } else if (me.getSource() instanceof Ellipse) {
                    ((Ellipse) (me.getSource())).setTranslateX(newTranslateX);
                    ((Ellipse) (me.getSource())).setTranslateY(newTranslateY);
                } else if (me.getSource() instanceof Polygon) {
                    ((Polygon) (me.getSource())).setTranslateX(newTranslateX);
                    ((Polygon) (me.getSource())).setTranslateY(newTranslateY);
                }

                if (me.getSource() instanceof Shape
                        && buffer[currentAction - 1] instanceof MoveAction) {
                    MoveAction ma = (MoveAction) buffer[currentAction - 1];
                    ma.newX = newTranslateX;
                    ma.newY = newTranslateY;
                }
            } else {
                drawingShape = true;
                if (modeChoice.getSelectedToggle() == toggleButtonStroke && path != null) {
                    LineTo lineTo = new LineTo(me.getX(), me.getY());
                    path.getElements().add(lineTo);
                } else if (modeChoice.getSelectedToggle() == toggleRectangular
                        && rect != null) {
                    double meX = me.getX();
                    double meY = me.getY();

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

                } else if (modeChoice.getSelectedToggle() == toggleButtonCircle && circ != null) {

                    double meX = me.getX();
                    double meY = me.getY();

                    if (rsX < meX) {
                        circ.setCenterX(rsX + (meX - rsX) / 2);
                        if (rsY < meY) {
                            circ.setCenterY(rsY + (meY - rsY) / 2);
                            circ.setRadius(Math.max(meX - circ.getCenterX(), meY - circ.getCenterY()));
                        } else {
                            circ.setCenterY(meY + (rsY - meY) / 2);
                            circ.setRadius(Math.max(meX - circ.getCenterX(), rsY - circ.getCenterY()));
                        }
                    } else {
                        circ.setCenterX(meX + (rsX - meX) / 2);
                        if (rsY < meY) {
                            circ.setCenterY(rsY + (meY - rsY) / 2);
                            circ.setRadius(Math.max(rsX - circ.getCenterX(), meY - circ.getCenterY()));
                        } else {
                            circ.setCenterY(meY + (rsY - meY) / 2);
                            circ.setRadius(Math.max(rsX - circ.getCenterX(), rsY - circ.getCenterY()));
                        }
                    }

                } else if (modeChoice.getSelectedToggle() == toggleButtonLine && line != null) {
                    double meX = me.getX();
                    double meY = me.getY();

                    line.setStartX(rsX);
                    line.setStartY(rsY);
                    line.setEndX(meX);
                    line.setEndY(meY);
                } else if (modeChoice.getSelectedToggle() == toggleButtonEllipse && ellipse != null) {

                    double meX = me.getX();
                    double meY = me.getY();

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
                } else if (modeChoice.getSelectedToggle() == toggleButtonSquare && square != null) {
                    double meX = me.getX();
                    double meY = me.getY();

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
                orgSceneX = me.getSceneX();
                orgSceneY = me.getSceneY();

                if (me.getSource() instanceof Rectangle) {
                    orgTranslateX = ((Rectangle)
                            (me.getSource())).getTranslateX();
                    orgTranslateY = ((Rectangle)
                            (me.getSource())).getTranslateY();
                } else if (me.getSource() instanceof Path) {
                    orgTranslateX = ((Path) (me.getSource())).getTranslateX();
                    orgTranslateY = ((Path) (me.getSource())).getTranslateY();
                } else if (me.getSource() instanceof Circle) {
                    orgTranslateX = ((Circle) (me.getSource())).getTranslateX();
                    orgTranslateY = ((Circle) (me.getSource())).getTranslateY();
                } else if (me.getSource() instanceof Line) {
                    orgTranslateX = ((Line) (me.getSource())).getTranslateX();
                    orgTranslateY = ((Line) (me.getSource())).getTranslateY();
                } else if (me.getSource() instanceof Ellipse) {
                    orgTranslateX = ((Ellipse) (me.getSource())).getTranslateX();
                    orgTranslateY = ((Ellipse) (me.getSource())).getTranslateY();
                }
                if (me.getSource() instanceof Shape) {
                    MoveAction ma = new MoveAction((Shape) me.getSource());
                    ma.oldX = orgTranslateX;
                    ma.oldY = orgTranslateY;
                    buffer[currentAction] = ma;
                    currentAction++;
                    maxAction = currentAction;
                }
            } else {

                if (modeChoice.getSelectedToggle() == toggleButtonStroke) {

                    path = new Path();

                    buffer[currentAction] = new DrawAction(path);
                    currentAction++;
                    maxAction = currentAction;

                    path.setStrokeWidth(sampleLine.getStrokeWidth());
                    path.setStroke(sampleLine.getStroke());

                    path.setOnMousePressed(pressHandler);
                    path.setOnMouseDragged(drugHandler);
                    path.setOnMouseEntered(enterHandler);
                    path.setOnMouseExited(exitHandler);
                    path.setOnMouseClicked(clickHandler);

                    canvas.getChildren().add(path);
                    path.getElements().add(
                            new MoveTo(me.getX(), me.getY()));

                } else if (modeChoice.getSelectedToggle() == toggleRectangular) {

                    // Rectangle-Start
                    rsX = me.getX();
                    rsY = me.getY();

                    rect = new Rectangle(rsX, rsY, 0, 0);

                    if (fillBox.isSelected()) {
                        rect.setFill(sampleLine.getStroke());
                    } else {
                        rect.setFill(Color.TRANSPARENT);
                        rect.setStroke(sampleLine.getStroke());
                        rect.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(rect);

                    buffer[currentAction] = new DrawAction(rect);
                    currentAction++;
                    maxAction = currentAction;

                    rect.setOnMousePressed(pressHandler);
                    rect.setOnMouseDragged(drugHandler);
                    rect.setOnMouseClicked(clickHandler);
                    rect.setOnMouseEntered(enterHandler);
                    rect.setOnMouseExited(exitHandler);
                } else if (modeChoice.getSelectedToggle() == toggleButtonCircle) {

                    //Circle drawing
                    rsX = me.getX();
                    rsY = me.getY();
                    if (fillBox.isSelected()) {
                        circ = new Circle(0, sampleLine.getStroke());
                    } else {
                        circ = new Circle(0, Color.TRANSPARENT);
                        circ.setStroke(sampleLine.getStroke());
                        circ.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(circ);
                    buffer[currentAction] = new DrawAction(circ);
                    currentAction++;
                    maxAction = currentAction;

                    circ.setOnMousePressed(pressHandler);
                    circ.setOnMouseDragged(drugHandler);
                    circ.setOnMouseClicked(clickHandler);
                    circ.setOnMouseEntered(enterHandler);
                    circ.setOnMouseExited(exitHandler);

                } else if (modeChoice.getSelectedToggle() == toggleButtonLine) {

                    //Line drawing
                    rsX = me.getX();
                    rsY = me.getY();

                    line = new Line(rsX, rsY, rsX, rsY);
                    line.setStrokeWidth(sampleLine.getStrokeWidth());
                    line.setStroke(sampleLine.getStroke());
                    canvas.getChildren().add(line);
                    buffer[currentAction] = new DrawAction(line);
                    currentAction++;
                    maxAction = currentAction;

                    line.setOnMousePressed(pressHandler);
                    line.setOnMouseDragged(drugHandler);
                    line.setOnMouseClicked(clickHandler);
                    line.setOnMouseEntered(enterHandler);
                    line.setOnMouseExited(exitHandler);

                } else if (modeChoice.getSelectedToggle() == toggleButtonEllipse) {

                    //Ellipse drawing
                    rsX = me.getX();
                    rsY = me.getY();
                    ellipse = new Ellipse(0, 0);

                    if (fillBox.isSelected()) {
                        ellipse.setFill(sampleLine.getStroke());
                    } else {
                        ellipse.setFill(Color.TRANSPARENT);
                        ellipse.setStroke(sampleLine.getStroke());
                        ellipse.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(ellipse);
                    buffer[currentAction] = new DrawAction(ellipse);
                    currentAction++;
                    maxAction = currentAction;

                    ellipse.setOnMousePressed(pressHandler);
                    ellipse.setOnMouseDragged(drugHandler);
                    ellipse.setOnMouseClicked(clickHandler);
                    ellipse.setOnMouseEntered(enterHandler);
                    ellipse.setOnMouseExited(exitHandler);

                } else if (modeChoice.getSelectedToggle() == toggleButtonSquare) {

                    //Ellipse drawing
                    rsX = me.getX();
                    rsY = me.getY();
                    square = new Rectangle(0, 0);

                    if (fillBox.isSelected()) {
                        square.setFill(sampleLine.getStroke());
                    } else {
                        square.setFill(Color.TRANSPARENT);
                        square.setStroke(sampleLine.getStroke());
                        square.setStrokeWidth(sampleLine.getStrokeWidth());
                    }
                    canvas.getChildren().add(square);
                    buffer[currentAction] = new DrawAction(square);
                    currentAction++;
                    maxAction = currentAction;

                    square.setOnMousePressed(pressHandler);
                    square.setOnMouseDragged(drugHandler);
                    square.setOnMouseClicked(clickHandler);
                    square.setOnMouseEntered(enterHandler);
                    square.setOnMouseExited(exitHandler);

                }
            }
        }
    };

    /**
     * Mouse event handler for MouseEvent.MOUSE_RELEASED events.
     */
    EventHandler<MouseEvent> releaseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (modeChoice.getSelectedToggle() == toggleButtonStroke) {
                path = null;
            } else if (modeChoice.getSelectedToggle() == toggleRectangular) {
                rect = null;
            } else if (modeChoice.getSelectedToggle() == toggleButtonCircle) {
                circ = null;
            } else if (modeChoice.getSelectedToggle() == toggleButtonLine) {
                line = null;
            } else if (modeChoice.getSelectedToggle() == toggleButtonEllipse) {
                ellipse = null;
            }
        }
    };

    /**
     * Mouse event handler for MouseEvent.MouseEvent.MOUSE_ENTERED events.
     */
    EventHandler<MouseEvent> enterHandler = me -> {
        if (dirMode) {
            if (me.getSource() instanceof Rectangle) {
                ((Rectangle) me.getSource()).setEffect(shadow);
            } else if (me.getSource() instanceof Path) {
                ((Path) me.getSource()).setEffect(shadow);
            } else if (me.getSource() instanceof Circle) {
                ((Circle) me.getSource()).setEffect(shadow);
            } else if (me.getSource() instanceof Line) {
                ((Line) me.getSource()).setEffect(shadow);
            } else if (me.getSource() instanceof Ellipse) {
                ((Ellipse) me.getSource()).setEffect(shadow);
            }
        }
    };

    /**
     * Mouse event handler for MouseEvent.MouseEvent.MOUSE_EXITED events.
     */
    EventHandler<MouseEvent> exitHandler = me -> {
        if (me.getSource() instanceof Rectangle) {
            ((Rectangle) me.getSource()).setEffect(null);
        } else if (me.getSource() instanceof Path) {
            ((Path) me.getSource()).setEffect(null);
        } else if (me.getSource() instanceof Circle) {
            ((Circle) me.getSource()).setEffect(null);
        } else if (me.getSource() instanceof Line) {
            ((Line) me.getSource()).setEffect(null);
        } else if (me.getSource() instanceof Ellipse) {
            ((Ellipse) me.getSource()).setEffect(null);
        }
    };

    /**
     * Re-do undone action.
     */
    private void redo() {
        if (currentAction < maxAction) {
            Action action = buffer[currentAction];
            if (action instanceof DrawAction) {
                DrawAction dA = (DrawAction) action;
                canvas.getChildren().add(dA.shape);
            } else if (action instanceof EraseAction) {
                EraseAction dA = (EraseAction) action;
                canvas.getChildren().remove(dA.shape);
            } else if (action instanceof MoveAction) {
                MoveAction dA = (MoveAction) action;
                Shape shape = dA.shape;
                shape.setTranslateX(dA.newX);
                shape.setTranslateY(dA.newY);
            }
            currentAction++;
        }
    }

    /**
     * Undo action.
     */
    private void undo() {
        if (currentAction > 0) {
            currentAction--;
            Action action = buffer[currentAction];
            if (action instanceof DrawAction) {
                DrawAction dA = (DrawAction) action;
                canvas.getChildren().remove(dA.shape);
            } else if (action instanceof EraseAction) {
                EraseAction dA = (EraseAction) action;
                canvas.getChildren().add(dA.shape);
            } else if (action instanceof MoveAction) {
                MoveAction dA = (MoveAction) action;
                Shape shape = dA.shape;
                shape.setTranslateX(dA.oldX);
                shape.setTranslateY(dA.oldY);
            }
        }
    }
}

/**
 * General Action interface for holding different actions together.
 */
interface Action { }

/**
 * Class for action of drawing a shape.
 */
class DrawAction implements Action {

    /**
     * Shape that was drawn.
     */
    Shape shape;

    /**
     * Constructor of the DrawAction object.
     * @param newShape Shape that was drawn.
     */
    public DrawAction(Shape newShape) {
        shape = newShape;
    }
}

/**
 * Class for action of erasing a shape.
 */
class EraseAction implements Action {

    /**
     * Shape that was erased.
     */
    Shape shape;

    /**
     * Constructor of the EraseAction object.
     * @param newShape Shape that was erased.
     */
    public EraseAction(Shape newShape) {
        shape = newShape;
    }
}

/**
 * Class for action of moving a shape.
 */
class MoveAction implements Action {

    /**
     * Shape that was moved.
     */
    Shape shape;

    /**
     * Shape old and new coordinates.
     */
    double oldX, oldY, newX, newY;

    /**
     * Constructor of the MoveAction object.
     * @param newShape Shape that was moved.
     */
    public MoveAction(Shape newShape) {
        shape = newShape;
    }
}
