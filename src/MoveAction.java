import javafx.scene.shape.Shape;

/**
 * Class for action of moving a shape.
 */
public class MoveAction implements Action {

    /**
     * Shape that was moved.
     */
    Shape shape;

    /**
     * Shape old and new coordinates.
     */
    Point2D oldPoint;
    Point2D newPoint;

    /**
     * Constructor of the MoveAction object.
     * @param newShape Shape that was moved.
     */
    public MoveAction(Shape newShape) {
        shape = newShape;
    }

    @Override
    public void undo() {
        if (oldPoint != null) {
            shape.setTranslateX(oldPoint.getX());
            shape.setTranslateY(oldPoint.getY());
        }
    }

    @Override
    public void redo() {
        if (newPoint != null) {
            shape.setTranslateX(newPoint.getX());
            shape.setTranslateY(newPoint.getY());
        }
    }

    public void setOldPoint(Point2D oldPoint) {
        this.oldPoint = oldPoint;
    }

    public void setNewPoint(Point2D newPoint) {
        this.newPoint = newPoint;
    }
}
