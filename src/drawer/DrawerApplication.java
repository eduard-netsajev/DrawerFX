package drawer;

import drawer.buffer.ActionBuffer;
import drawer.mode.UsageMode;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public interface DrawerApplication {

    Pane getCanvas();

    ObservableBooleanValue getFillShapeProperty();

    UsageMode getUsageMode();

    ActionBuffer getBuffer();

    void registerShapeHandlers(Shape shape);

    Line getSampleLine();

    ShapeMode getShapeMode();
}