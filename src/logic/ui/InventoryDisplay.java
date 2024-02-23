package logic.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class InventoryDisplay implements Display {
    private VBox view;

    public InventoryDisplay() {
        // Initialize the display layout
        view = new VBox();
        Label label = new Label("Inventory Display");
        label.setTextFill(Color.WHITE);
        view.getChildren().add(label);
        // Add more components as needed
    }

    @Override
    public Node getView() {
        return view;
    }
}
