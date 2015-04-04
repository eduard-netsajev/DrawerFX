package drawer.button;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.io.File;

public class SaveButton extends Button {

    Pane canvas;

    private static final int MILLISECS_IN_SEC = 1000;

    public SaveButton(Pane canvas) {
        super("Save");
        this.canvas = canvas;
        this.setOnAction(event -> saveImagePNG(canvas));
    }

    private void saveImagePNG(Pane canvas) {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);
        WritableImage image = canvas.snapshot(snapshotParameters, null);
        File file = new File(String.format("saved_%d.png", System.currentTimeMillis() / MILLISECS_IN_SEC));
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Saved image at " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Exception while saving image: " + e.getMessage());
        }
    }
}