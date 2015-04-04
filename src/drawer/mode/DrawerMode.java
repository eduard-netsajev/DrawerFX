package drawer.mode;

import drawer.DrawerApplication;
import drawer.ShapeMode;
import drawer.action.DrawAction;
import drawer.buffer.ActionBuffer;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class DrawerMode implements UsageMode {

    DrawerApplication application;

    private Pane canvas;
    private ActionBuffer buffer;
    private boolean drawingShape;
    private Line sampleLine;

    private Shape shape;
    private double shapeStartX, shapeStartY;
    private ObservableBooleanValue fillShape;

    public DrawerMode(DrawerApplication application) {
        this.application = application;
        updateFields();
    }

    private void updateFields() {
        canvas = application.getCanvas();
        buffer = application.getBuffer();
        sampleLine = application.getSampleLine();
        fillShape = application.getFillShapeProperty();
    }

    @Override
    public void handleClick(MouseEvent me) {
        updateFields();
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
        buffer.add(new DrawAction(canvas, point));
        point.setFill(sampleLine.getStroke());
        application.registerShapeHandlers(point);
        canvas.getChildren().add(point);
    }

    @Override
    public void handleDrag(MouseEvent me) {
        updateFields();
        ShapeMode mode = application.getShapeMode();

        drawingShape = true;
        if (mode == ShapeMode.STROKE && shape != null) {
            LineTo lineTo = new LineTo(me.getX(), me.getY());
            ((Path) shape).getElements().add(lineTo);
        } else if (mode == ShapeMode.RECTANGULAR
                && shape != null) {
            double meX = me.getX();
            double meY = me.getY();

            Rectangle rect = ((Rectangle) shape);

            if (shapeStartX < meX) {
                if (shapeStartY < meY) {

                    rect.setX(shapeStartX);
                    rect.setY(shapeStartY);
                    rect.setWidth(meX - shapeStartX);
                    rect.setHeight(meY - shapeStartY);
                } else {
                    rect.setX(shapeStartX);
                    rect.setY(meY);
                    rect.setWidth(meX - shapeStartX);
                    rect.setHeight(shapeStartY - meY);
                }
            } else {
                if (shapeStartY < meY) {

                    rect.setX(meX);
                    rect.setY(shapeStartY);
                    rect.setWidth(shapeStartX - meX);
                    rect.setHeight(meY - shapeStartY);
                } else {
                    rect.setX(meX);
                    rect.setY(meY);
                    rect.setWidth(shapeStartX - meX);
                    rect.setHeight(shapeStartY - meY);
                }
            }

        } else if (mode == ShapeMode.CIRCLE && shape != null) {

            double meX = me.getX();
            double meY = me.getY();

            Circle circle = ((Circle) shape);

            if (shapeStartX < meX) {
                circle.setCenterX(shapeStartX + (meX - shapeStartX) / 2);
                if (shapeStartY < meY) {
                    circle.setCenterY(shapeStartY + (meY - shapeStartY) / 2);
                    circle.setRadius(Math.max(meX - circle.getCenterX(), meY - circle.getCenterY()));
                } else {
                    circle.setCenterY(meY + (shapeStartY - meY) / 2);
                    circle.setRadius(Math.max(meX - circle.getCenterX(), shapeStartY - circle.getCenterY()));
                }
            } else {
                circle.setCenterX(meX + (shapeStartX - meX) / 2);
                if (shapeStartY < meY) {
                    circle.setCenterY(shapeStartY + (meY - shapeStartY) / 2);
                    circle.setRadius(Math.max(shapeStartX - circle.getCenterX(), meY - circle.getCenterY()));
                } else {
                    circle.setCenterY(meY + (shapeStartY - meY) / 2);
                    circle.setRadius(Math.max(shapeStartX - circle.getCenterX(), shapeStartY - circle.getCenterY()));
                }
            }

        } else if (mode == ShapeMode.LINE && shape != null) {
            double meX = me.getX();
            double meY = me.getY();

            Line line = ((Line) shape);
            line.setStartX(shapeStartX);
            line.setStartY(shapeStartY);
            line.setEndX(meX);
            line.setEndY(meY);
        } else if (mode == ShapeMode.ELLIPSE && shape != null) {

            double meX = me.getX();
            double meY = me.getY();

            Ellipse ellipse = ((Ellipse) shape);

            if (shapeStartX < meX) {
                ellipse.setCenterX(shapeStartX + (meX - shapeStartX) / 2);
                ellipse.setRadiusX(meX - ellipse.getCenterX());
                if (shapeStartY < meY) {
                    ellipse.setCenterY(shapeStartY + (meY - shapeStartY) / 2);
                    ellipse.setRadiusY(meY - ellipse.getCenterY());
                } else {
                    ellipse.setCenterY(meY + (shapeStartY - meY) / 2);
                    ellipse.setRadiusY(shapeStartY - ellipse.getCenterY());
                }
            } else {
                ellipse.setCenterX(meX + (shapeStartX - meX) / 2);
                ellipse.setRadiusX(shapeStartX - ellipse.getCenterX());
                if (shapeStartY < meY) {
                    ellipse.setCenterY(shapeStartY + (meY - shapeStartY) / 2);
                    ellipse.setRadiusY(meY - ellipse.getCenterY());
                } else {
                    ellipse.setCenterY(meY + (shapeStartY - meY) / 2);
                    ellipse.setRadiusY(shapeStartY - ellipse.getCenterY());
                }
            }
        } else if (mode == ShapeMode.SQUARE && shape != null) {
            double meX = me.getX();
            double meY = me.getY();

            Rectangle square = ((Rectangle) shape);

            if (shapeStartX < meX) {
                if (shapeStartY < meY) {
                    // lower right
                    square.setX(shapeStartX);
                    square.setY(shapeStartY);
                    square.setWidth(meX - shapeStartX);
                    square.setHeight(meX - shapeStartX);
                } else {
                    // upper right
                    square.setX(shapeStartX);
                    square.setY(meY);
                    square.setWidth(shapeStartY - meY);
                    square.setHeight(shapeStartY - meY);
                }
            } else {
                if (shapeStartY < meY) {
                    // lower left
                    square.setX(meX);
                    square.setY(shapeStartY);
                    square.setWidth(shapeStartX - meX);
                    square.setHeight(shapeStartX - meX);
                } else {
                    // upper right
                    square.setX(meX);
                    square.setY(shapeStartY - shapeStartX + meX);
                    square.setWidth(shapeStartX - meX);
                    square.setHeight(shapeStartX - meX);
                }
            }
        }
    }

    @Override
    public void handlePress(MouseEvent me) {
        updateFields();
        ShapeMode mode = application.getShapeMode();
        shapeStartX = me.getX();
        shapeStartY = me.getY();

        if (mode == ShapeMode.STROKE) {

            Path path = new Path();

            buffer.add(new DrawAction(canvas, path));

            path.setStrokeWidth(sampleLine.getStrokeWidth());
            path.setStroke(sampleLine.getStroke());

            application.registerShapeHandlers(path);
            canvas.getChildren().add(path);
            path.getElements().add(new MoveTo(shapeStartX, shapeStartY));
            shape = path;

        } else if (mode == ShapeMode.RECTANGULAR) {

            Rectangle rect = new Rectangle(shapeStartX, shapeStartY, 0, 0);

            if (fillShape.get()) {
                rect.setFill(sampleLine.getStroke());
            } else {
                rect.setFill(Color.TRANSPARENT);
                rect.setStroke(sampleLine.getStroke());
                rect.setStrokeWidth(sampleLine.getStrokeWidth());
            }
            canvas.getChildren().add(rect);
            buffer.add(new DrawAction(canvas, rect));
            application.registerShapeHandlers(rect);
            shape = rect;
        } else if (mode == ShapeMode.CIRCLE) {

            Circle circle;

            if (fillShape.get()) {
                circle = new Circle(0, sampleLine.getStroke());
            } else {
                circle = new Circle(0, Color.TRANSPARENT);
                circle.setStroke(sampleLine.getStroke());
                circle.setStrokeWidth(sampleLine.getStrokeWidth());
            }
            canvas.getChildren().add(circle);
            buffer.add(new DrawAction(canvas, circle));
            application.registerShapeHandlers(circle);
            shape = circle;

        } else if (mode == ShapeMode.LINE) {

            Line line = new Line(shapeStartX, shapeStartY, shapeStartX, shapeStartY);
            line.setStrokeWidth(sampleLine.getStrokeWidth());
            line.setStroke(sampleLine.getStroke());
            canvas.getChildren().add(line);
            buffer.add(new DrawAction(canvas, line));
            application.registerShapeHandlers(line);
            shape = line;

        } else if (mode == ShapeMode.ELLIPSE) {

            Ellipse ellipse = new Ellipse(0, 0);

            if (fillShape.get()) {
                ellipse.setFill(sampleLine.getStroke());
            } else {
                ellipse.setFill(Color.TRANSPARENT);
                ellipse.setStroke(sampleLine.getStroke());
                ellipse.setStrokeWidth(sampleLine.getStrokeWidth());
            }
            canvas.getChildren().add(ellipse);
            buffer.add(new DrawAction(canvas, ellipse));
            application.registerShapeHandlers(ellipse);
            shape = ellipse;

        } else if (mode == ShapeMode.SQUARE) {

            Rectangle square = new Rectangle(0, 0);

            if (fillShape.get()) {
                square.setFill(sampleLine.getStroke());
            } else {
                square.setFill(Color.TRANSPARENT);
                square.setStroke(sampleLine.getStroke());
                square.setStrokeWidth(sampleLine.getStrokeWidth());
            }
            canvas.getChildren().add(square);
            buffer.add(new DrawAction(canvas, square));
            application.registerShapeHandlers(square);
            shape = square;
        }
    }

    @Override
    public void handleRelease(MouseEvent me) {
        shape = null;
    }

    @Override
    public void handleEnter(MouseEvent me) {
        // do nothing
    }

    @Override
    public void handleExit(MouseEvent me) {
        // do nothing
    }
}
