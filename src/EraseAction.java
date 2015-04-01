import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * Class for action of erasing a shape.
 */
public class EraseAction implements Action {

    /**
     * Shape that was erased.
     */
    Shape shape;

    Pane canvas;

    /**
     * Constructor of the EraseAction object.
     * @param newShape Shape that was erased.
     */
    public EraseAction(Pane canvas, Shape newShape) {
        this.canvas = canvas;
        this.shape = newShape;
    }

    @Override
    public void undo() {
        canvas.getChildren().add(shape);
    }

    @Override
    public void redo() {
        canvas.getChildren().remove(shape);
    }
}
