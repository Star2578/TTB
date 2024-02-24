package logic.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ItemSelectDisplay implements Display{
    private VBox view;

    public ItemSelectDisplay() {
        view = new VBox();
        Label label = new Label("Item Select Display");
        label.setTextFill(Color.WHITE);
        view.getChildren().add(label);
    }

    @Override
    public Node getView() {
        return view;
    }
}
