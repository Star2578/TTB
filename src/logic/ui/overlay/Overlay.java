package logic.ui.overlay;

import javafx.scene.layout.VBox;

public class Overlay {
    protected VBox view;
    private double offsetX;
    private double offsetY;

    public Overlay (int width, int height) {
        view = new VBox();

        setViewSize(width, height);
    }


    // Method to update the position of the overlay
    public void updatePosition(double x, double y, double offsetX, double offsetY) {
        // Adjust the layout parameters of the VBox to position it at (x, y)
        this.offsetX = view.getWidth() + offsetX;
        this.offsetY = view.getHeight() + offsetY;
        view.setTranslateX(x - this.offsetX);
        view.setTranslateY(y - this.offsetY);
    }

    public void setViewSize(int width, int height) {
        view.setMaxWidth(width);
        view.setMaxHeight(height);
    }

    public VBox getView() {
        return view;
    }
}
