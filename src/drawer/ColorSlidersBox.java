package drawer;

import drawer.SlidersColorBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ColorSlidersBox extends VBox {

    private static final Integer MAX_RGB = 255;

    private static final Integer MIN_RGB = 0;

    private static final Integer DEFAULT_RED = 0;

    private static final Integer DEFAULT_GREEN = 0;

    private static final Integer DEFAULT_BLUE = 255;

    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;

    public ColorSlidersBox() {
        super(10);
        this.setAlignment(Pos.TOP_CENTER);
        setupSliders();
        setupBoxes();
    }

    private void setupSliders() {
        redSlider = createSlider(DEFAULT_RED);
        greenSlider = createSlider(DEFAULT_GREEN);
        blueSlider = createSlider(DEFAULT_BLUE);
    }

    private void setupBoxes() {
        HBox redSliderBox = createColorBox("R", redSlider);
        HBox greenSliderBox = createColorBox("G", greenSlider);
        HBox blueSliderHBox = createColorBox("B", blueSlider);

        this.getChildren().addAll(redSliderBox, greenSliderBox, blueSliderHBox);
    }

    private Slider createSlider(int startValue) {
        return new Slider(MIN_RGB, MAX_RGB, startValue);
    }

    private HBox createColorBox(String alias, Slider slider) {
        Label label = new Label(alias);
        HBox sliderBox = new HBox(5);
        sliderBox.getChildren().addAll(label, slider);
        return sliderBox;
    }

    public SlidersColorBinding getSlidersColorBinding() {
        return new SlidersColorBinding(redSlider, greenSlider, blueSlider);
    }
}
