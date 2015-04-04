package drawer;

import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class SlidersColorBinding extends ObjectBinding<Paint> {

    Slider red;
    Slider green;
    Slider blue;

    public SlidersColorBinding(Slider red, Slider green, Slider blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;

        super.bind(red.valueProperty(),
                green.valueProperty(),
                blue.valueProperty());
    }

    @Override
    protected Paint computeValue() {
        return Color.rgb(red.valueProperty().intValue(),
                green.valueProperty().intValue(),
                blue.valueProperty().intValue());
    }
}
