package drawer;

import drawer.mode.UsageMode;
import javafx.scene.input.MouseEvent;

public class MouseEventDelegator implements UsageMode {

    private DrawerApplication application;

    public MouseEventDelegator(DrawerApplication application) {
        this.application = application;
    }

    public void handleClick(MouseEvent me) {
        application.getUsageMode().handleClick(me);
    }

    public void handleDrag(MouseEvent me) {
        application.getUsageMode().handleDrag(me);
    }

    public void handlePress(MouseEvent me) {
        application.getUsageMode().handlePress(me);
    }

    public void handleEnter(MouseEvent me) {
        application.getUsageMode().handleEnter(me);
    }

    public void handleRelease(MouseEvent me) {
        application.getUsageMode().handleRelease(me);
    }

    public void handleExit(MouseEvent me) {
        application.getUsageMode().handleExit(me);
    }
}
