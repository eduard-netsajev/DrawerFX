package drawer.action;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/**
 * Class for action of drawing a shape.
 */
public class DrawAction implements Action {

    /**
     * Shape that was drawn.
     */
    Shape shape;

    Pane canvas;

    /**
     * Constructor of the DrawAction object.
     * @param newShape Shape that was drawn.
     */
    public DrawAction(Pane canvas, Shape newShape) {
        this.canvas = canvas;
        this.shape = newShape;
    }

    @Override
    public void undo() {
        canvas.getChildren().remove(shape);
    }

    @Override
    public void redo() {
        canvas.getChildren().add(shape);
    }
}
