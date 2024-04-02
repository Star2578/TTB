package logic.ui.overlay;

import javafx.scene.layout.VBox;

public class Overlay {
    protected VBox view;

    public Overlay (int width, int height) {
        view = new VBox();

        setViewSize(width, height);
    }


    // Method to update the position of the overlay
    public void updatePosition(double x, double y) {
        // Adjust the layout parameters of the VBox to position it at (x, y)
        double offsetX = view.getWidth() - 140;
        double offsetY = view.getHeight() + 15;
        view.setTranslateX(x - offsetX);
        view.setTranslateY(y - offsetY);
    }

    public void setViewSize(int width, int height) {
        view.setMaxWidth(width);
        view.setMaxHeight(height);
    }

    public VBox getView() {
        return view;
    }
}
