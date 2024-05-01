package logic.ui.overlay;

import javafx.scene.layout.VBox;
import logic.SceneManager;

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
        // Calculate the total offset including the additional offset for the application border
        double totalOffsetX = this.offsetX + view.getWidth() + offsetX;
        double totalOffsetY = this.offsetY + view.getHeight() + offsetY;

        // Adjust the layout parameters of the VBox to position it at (x, y)
        double newX = x - totalOffsetX;
        double newY = y - totalOffsetY;

        int windowWidth = SceneManager.getInstance().getScreenWidth();
        int windowHeight = SceneManager.getInstance().getScreenHeight();

        // Check if the new position is within the application window
        if (newX < 0) {
            newX = 0; // Adjust to the left edge
        } else if (newX + view.getWidth() > windowWidth) {
            newX = windowWidth - view.getWidth(); // Adjust to the right edge
        }

        if (newY < 0) {
            newY = 0; // Adjust to the top edge
        } else if (newY + view.getHeight() > windowHeight) {
            newY = windowHeight - view.getHeight(); // Adjust to the bottom edge
        }

        // Set the new translated position
        view.setTranslateX(newX);
        view.setTranslateY(newY);
    }

    public void setViewSize(int width, int height) {
        view.setMaxWidth(width);
        view.setMaxHeight(height);
    }

    public VBox getView() {
        return view;
    }
}
