package drawer.action;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.InputMismatchException;

public class ApplicationStartAction implements Action {

    @Override
    public void undo() {
        Stage stage = new Stage();
        stage.setTitle("Exit Confirmation");

        BorderPane parentPane = new BorderPane(getCenterBox(stage));
        stage.setScene(new Scene(parentPane));
        stage.show();
    }

    private VBox getCenterBox(Stage stage) {
        VBox pane = new VBox(10);
        Label question = new Label("Are you sure you want to leave  the program?");
        HBox choice = getChoiceBox(stage);
        pane.getChildren().addAll(question, choice);
        return pane;
    }

    private HBox getChoiceBox(Stage stage) {
        HBox choice = new HBox(10);
        Button no = getNoButton(stage);
        Button yes = getYestButton();
        choice.getChildren().addAll(yes, no);
        return choice;
    }

    private Button getYestButton() {
        Button yes = new Button("Yes");
        yes.setOnAction(event -> System.exit(0));
        return yes;
    }

    private Button getNoButton(Stage stage) {
        Button no = new Button("No");
        no.setOnAction(event -> stage.close());
        return no;
    }

    @Override
    public void redo() {
        throw new InputMismatchException("You can't call redo on Start Application action");
    }
}
