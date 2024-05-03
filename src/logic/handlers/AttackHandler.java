package logic.handlers;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import utils.ImageScaler;
import pieces.players.BasePlayerPiece;
import utils.Config;

import java.util.ArrayList;

public class AttackHandler {
    private static GameManager gameManager = GameManager.getInstance();
    private static BasePlayerPiece player;
    private static boolean[][] validMovesCache;
    private static ArrayList<Point2D> selectedTiles;
    private static ImageView[][] selectionFloor;
    private static final int BOARD_SIZE = Config.BOARD_SIZE;

    public static void showValidAttackRange(int playerRow, int playerCol) {
        player = gameManager.player;
        selectedTiles = gameManager.selectedAttackTiles;
        validMovesCache = gameManager.validMovesCache;
        selectionFloor = gameManager.selectionFloor;

        int attackRange = player.getAttackRange(); // Change this according to the player's attack range

        for (int dRow = -attackRange; dRow <= attackRange; dRow++) {
            for (int dCol = -attackRange; dCol <= attackRange; dCol++) {
                int newRow = playerRow + dRow;
                int newCol = playerCol + dCol;
                // Check if the new position is within the board bounds and not the current position
                if (isInBoardPosition(newRow, newCol) && (newRow != playerRow || newCol != playerCol)) {
                    // Check if the square is within the attack range using the player's validAttack method
                    if (validMovesCache[newRow][newCol] && player.validAttack(newRow, newCol)) {
                        selectedTiles.add(new Point2D(newRow , newCol));
                        // Highlight or mark the square to indicate it's within the attack range
                        selectionFloor[newRow][newCol].setImage(ImageScaler.resample(new Image(Config.ValidAttackPath), 2)); // Set texture to indicate valid attack
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
