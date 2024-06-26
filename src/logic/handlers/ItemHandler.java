package logic.handlers;

import items.BaseItem;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import utils.ImageScaler;
import utils.Config;
import items.Usable;

import java.util.ArrayList;

public class ItemHandler {
    private static GameManager gameManager = GameManager.getInstance();
    private static boolean[][] validMovesCache;
    private static ArrayList<Point2D> availableTiles;
    private static ImageView[][] selectionFloor;
    private static final int BOARD_SIZE = Config.BOARD_SIZE;

    public static void showValidItemRange(int playerRow, int playerCol, BaseItem itemSelected) {
        selectionFloor = gameManager.selectionFloor;
        availableTiles = gameManager.availableItemTiles;
        validMovesCache = gameManager.validMovesCache;

        if (itemSelected instanceof Usable usableItem) {
            int range = usableItem.getRange();

            for (int dRow = -range; dRow <= range; dRow++) {
                for (int dCol = -range; dCol <= range; dCol++) {
                    int newRow = playerRow + dRow;
                    int newCol = playerCol + dCol;
                    if (validMovesCache[newRow][newCol] && isInBoardPosition(newRow, newCol) && usableItem.validRange(newRow, newCol)) {
                        if (!usableItem.castOnSelf()) {
                            if ((newRow == playerRow && newCol == playerCol)) continue;
                        }
                        availableTiles.add(new Point2D(newRow, newCol));
                        // Highlight or mark the square to indicate it's within the skill range
                        selectionFloor[newRow][newCol].setImage(ImageScaler.resample(new Image(Config.ValidItemPath), 2)); // Set texture to indicate valid skill
                    }
                }
            }
        }
    }

    private static boolean isInBoardPosition(int row, int col) {
        // Check if the position is inside the board
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
}
