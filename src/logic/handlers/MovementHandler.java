package logic.handlers;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import logic.ImageScaler;
import pieces.BasePiece;
import utils.Config;

import java.util.ArrayList;

public class MovementHandler {
    private static final int BOARD_SIZE = Config.BOARD_SIZE;
    private static GameManager gameManager = GameManager.getInstance();
    private static BasePiece[][] piecesPosition = gameManager.piecesPosition;
    private static boolean[][] validMovesCache = gameManager.validMovesCache;
    private static ArrayList<Point2D> selectedTiles = gameManager.selectedTiles;
    private static ImageView[][] dungeonFloor = gameManager.dungeonFloor;
    private static ImageScaler imageScaler = new ImageScaler();

    public static void showValidMoves(int row, int col) {
        // Iterate over adjacent squares and update images based on cached valid moves
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                int newRow = row + dRow;
                int newCol = col + dCol;
                // Check if the new position is within the board bounds and not the current position
                if (isInBoardPosition(newRow, newCol) && (newRow != row || newCol != col)) {
                    if (validMovesCache[newRow][newCol] && piecesPosition[newRow][newCol] == null) {
                        selectedTiles.add(new Point2D(newRow , newCol));
                        dungeonFloor[newRow][newCol].setImage(imageScaler.resample(new Image(Config.ValidMovePath), 2)); // Set texture to indicate valid move
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
