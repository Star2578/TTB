package logic.ui;

import javafx.scene.Node;

public interface Display {
    void initialize(); // Method to initialize the display
    Node getView(); // Method to get the root Node of the display
}
