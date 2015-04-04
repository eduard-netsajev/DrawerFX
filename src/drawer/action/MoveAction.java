package drawer.action;

import drawer.Point2D;
import javafx.scene.shape.Shape;

/**
 * Class for action of moving a shape.
 */
public class MoveAction implements Action {

    /**
     * Shape that was moved.
     */
    Shape shape;

    Point2D oldLayoutPoint;
    Point2D newLayoutPoint;

    Point2D originalMousePoint;

    /**
     * Constructor of the MoveAction object.
     * @param newShape Shape that was moved.
     */
    public MoveAction(Shape newShape) {
        shape = newShape;
    }

    @Override
    public void undo() {
        if (oldLayoutPoint != null) {
            shape.setLayoutX(oldLayoutPoint.getX());
            shape.setLayoutY(oldLayoutPoint.getY());
        }
    }

    @Override
    public void redo() {
        if (newLayoutPoint != null) {
            shape.setLayoutX(newLayoutPoint.getX());
            shape.setLayoutY(newLayoutPoint.getY());
        }
    }

    public void setOldLayoutPoint(Point2D oldLayoutPoint) {
        this.oldLayoutPoint = oldLayoutPoint;
    }

    public void setNewLayoutPoint(Point2D newLayoutPoint) {
        this.newLayoutPoint = newLayoutPoint;
    }

    public void setOriginalMousePoint(Point2D originalMousePoint) {
        this.originalMousePoint = originalMousePoint;
    }

    public Point2D getOriginalMousePoint() {
        return originalMousePoint;
    }
}