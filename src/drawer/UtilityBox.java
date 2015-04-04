package drawer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class UtilityBox extends VBox {

    public UtilityBox(Button clear, Slider strokeWidth, CheckBox fill) {
        super(10);
        Label labelStroke = new Label("Stroke Width");
        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().addAll(clear, labelStroke, strokeWidth, fill);
    }
}
