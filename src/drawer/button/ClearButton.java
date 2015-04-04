package drawer.button;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class ClearButton extends Button {

    public ClearButton(Pane canvas) {
        this.setText("Clear");
        this.setOnAction(e -> canvas.getChildren().removeAll(canvas.getChildren()));
    }
}
