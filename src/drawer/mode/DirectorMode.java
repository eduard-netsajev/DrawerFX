package drawer.mode;

import drawer.DrawerApplication;
import drawer.Point2D;
import drawer.action.Action;
import drawer.action.EraseAction;
import drawer.action.MoveAction;
import drawer.buffer.ActionBuffer;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class DirectorMode implements UsageMode {

    /**
     * Shadow effect for highlighting shapes.
     */
    private final DropShadow shadow = new DropShadow(15, Color.BLACK);

    private Pane canvas;
    private ActionBuffer buffer;

    public DirectorMode(DrawerApplication application) {
        this.canvas = application.getCanvas();
        this.buffer = application.getBuffer();
    }

    @Override
    public void handleClick(MouseEvent me) {
        if (me.getButton() == MouseButton.SECONDARY && me.getSource() instanceof Shape) {
            Shape shape = (Shape) me.getSource();
            canvas.getChildren().remove(shape);
            buffer.add(new EraseAction(canvas, shape));
        }
    }

    @Override
    public void handleDrag(MouseEvent me) {
        Action previousAction = buffer.peekPrevious();
        if (me.getSource() instanceof Shape && previousAction instanceof MoveAction) {
            MoveAction moveAction = (MoveAction) previousAction;

            Point2D originalMousePoint = moveAction.getOriginalMousePoint();
            Point2D offset = calculateOffset(me, originalMousePoint);

            Shape shape = ((Shape) me.getSource());
            Point2D newLayoutPoint = calculateNewLayout(shape, offset);

            shape.setLayoutX(newLayoutPoint.getX());
            shape.setLayoutY(newLayoutPoint.getY());

            moveAction.setNewLayoutPoint(newLayoutPoint);
        }
    }

    @Override
    public void handlePress(MouseEvent me) {
        if (me.getSource() instanceof Shape) {

            Shape shape = ((Shape) me.getSource());
            MoveAction moveAction = new MoveAction(shape);

            Point2D originalPoint = new Point2D(me.getX(), me.getY());
            moveAction.setOriginalMousePoint(originalPoint);

            Point2D oldLayoutPoint = new Point2D(shape.getLayoutX(), shape.getLayoutY());
            moveAction.setOldLayoutPoint(oldLayoutPoint);

            buffer.add(moveAction);
        }
    }

    @Override
    public void handleEnter(MouseEvent me) {
        if (me.getSource() instanceof Shape) {
            ((Shape) me.getSource()).setEffect(shadow);
        }
    }

    private Point2D calculateOffset(MouseEvent me, Point2D originalMousePoint) {
        double offsetX = me.getX() - originalMousePoint.getX();
        double offsetY = me.getY() - originalMousePoint.getY();
        return new Point2D(offsetX, offsetY);
    }

    private Point2D calculateNewLayout(Shape shape, Point2D offset) {
        double newLayoutX = shape.getLayoutX() + offset.getX();
        double newLayoutY = shape.getLayoutY() + offset.getY();
        return new Point2D(newLayoutX, newLayoutY);
    }

    @Override
    public void handleRelease(MouseEvent me) {
        //do nothing
    }

    @Override
    public void handleExit(MouseEvent me) {
        if (me.getSource() instanceof Shape)
            ((Shape) me.getSource()).setEffect(null);
    }

}