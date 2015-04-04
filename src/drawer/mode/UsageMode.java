package drawer.mode;

import javafx.scene.input.MouseEvent;

public interface UsageMode {

    public void handleClick(MouseEvent me);

    public void handleDrag(MouseEvent me);

    public void handlePress(MouseEvent me);

    public void handleEnter(MouseEvent me);

    public void handleRelease(MouseEvent me);

    public void handleExit(MouseEvent me);

}