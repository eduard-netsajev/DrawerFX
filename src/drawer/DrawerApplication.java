package drawer;

import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public interface DrawerApplication {

    Pane getCanvas();

    BooleanProperty getFillShapeProperty();

    UsageMode getUsageMode();

    EditHistoryBuffer getBuffer();

    void registerShapeHandlers(Shape shape);

    Line getSampleLine();

    ToggledShape getShapeMode();
}
