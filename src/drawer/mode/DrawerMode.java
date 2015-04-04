package drawer.mode;

import drawer.DrawerApplication;
import drawer.ToggledShape;
import drawer.action.DrawAction;
import drawer.buffer.ActionBuffer;
import javafx.beans.property.BooleanProperty;
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
    private double rsX, rsY;
    private BooleanProperty fillShape;

    public DrawerMode(DrawerApplication application) {
        this.canvas = application.getCanvas();
        this.buffer = application.getBuffer();
        this.sampleLine = application.getSampleLine();
        this.fillShape = application.getFillShapeProperty();
        this.application = application;
    }

    @Override
    public void handleClick(MouseEvent me) {
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

        ToggledShape mode = application.getShapeMode();

        drawingShape = true;
        if (mode == ToggledShape.STROKE && shape != null) {
            LineTo lineTo = new LineTo(me.getX(), me.getY());
            ((Path) shape).getElements().add(lineTo);
        } else if (mode == ToggledShape.RECTANGULAR
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

        } else if (mode == ToggledShape.CIRCLE && shape != null) {

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

        } else if (mode == ToggledShape.LINE && shape != null) {
            double meX = me.getX();
            double meY = me.getY();

            Line line = ((Line) shape);
            line.setStartX(rsX);
            line.setStartY(rsY);
            line.setEndX(meX);
            line.setEndY(meY);
        } else if (mode == ToggledShape.ELLIPSE && shape != null) {

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
        } else if (mode == ToggledShape.SQUARE && shape != null) {
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

    @Override
    public void handlePress(MouseEvent me) {
        ToggledShape mode = application.getShapeMode();

        if (mode == ToggledShape.STROKE) {

            Path path = new Path();

            buffer.add(new DrawAction(canvas, path));

            path.setStrokeWidth(sampleLine.getStrokeWidth());
            path.setStroke(sampleLine.getStroke());

            application.registerShapeHandlers(path);

            canvas.getChildren().add(path);
            path.getElements().add(
                    new MoveTo(me.getX(), me.getY()));

            shape = path;

        } else if (mode == ToggledShape.RECTANGULAR) {

            // Rectangle-Start
            rsX = me.getX();
            rsY = me.getY();

            Rectangle rect = new Rectangle(rsX, rsY, 0, 0);

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
        } else if (mode == ToggledShape.CIRCLE) {

            //Circle drawing
            rsX = me.getX();
            rsY = me.getY();

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

        } else if (mode == ToggledShape.LINE) {

            //Line drawing
            rsX = me.getX();
            rsY = me.getY();

            Line line = new Line(rsX, rsY, rsX, rsY);
            line.setStrokeWidth(sampleLine.getStrokeWidth());
            line.setStroke(sampleLine.getStroke());
            canvas.getChildren().add(line);

            buffer.add(new DrawAction(canvas, line));

            application.registerShapeHandlers(line);
            shape = line;

        } else if (mode == ToggledShape.ELLIPSE) {

            //Ellipse drawing
            rsX = me.getX();
            rsY = me.getY();
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

        } else if (mode == ToggledShape.SQUARE) {

            //Ellipse drawing
            rsX = me.getX();
            rsY = me.getY();
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
