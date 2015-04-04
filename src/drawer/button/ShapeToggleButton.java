package drawer.button;

import drawer.ShapeMode;
import javafx.scene.control.ToggleButton;

public class ShapeToggleButton extends ToggleButton {

    ShapeMode shapeMode;

    public ShapeToggleButton(String label, ShapeMode shapeMode) {
        super(label);
        this.shapeMode = shapeMode;
    }

    public ShapeMode getShapeMode() {
        return shapeMode;
    }

}
