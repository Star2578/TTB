package logic.handlers;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.GameManager;
import utils.ImageScaler;
import skills.BaseSkill;
import utils.Config;

import java.util.ArrayList;

public class SkillHandler {
    private static GameManager gameManager = GameManager.getInstance();
    private static boolean[][] validMovesCache;
    private static ArrayList<Point2D> availableTiles;
    private static ImageView[][] selectionFloor;
    private static final int BOARD_SIZE = Config.BOARD_SIZE;

    public static void showValidSkillRange(int playerRow, int playerCol, BaseSkill skillSelected) {
        selectionFloor = gameManager.selectionFloor;
        availableTiles = gameManager.availableSkillTiles;
        validMovesCache = gameManager.validMovesCache;
        int range = skillSelected.getRange();

        for (int dRow = -range; dRow <= range; dRow++) {
            for (int dCol = -range; dCol <= range; dCol++) {
                int newRow = playerRow + dRow;
                int newCol = playerCol + dCol;
                if (isInBoardPosition(newRow, newCol) && validMovesCache[newRow][newCol] && skillSelected.validRange(newRow, newCol)) {
                    if (!skillSelected.castOnSelf()) {
                        if ((newRow == playerRow && newCol == playerCol)) continue;
                    }
                    availableTiles.add(new Point2D(newRow, newCol));
                    // Highlight or mark the square to indicate it's within the skill range
                    selectionFloor[newRow][newCol].setImage(ImageScaler.resample(new Image(Config.ValidSkillPath), 2)); // Set texture to indicate valid skill
                }
            }
        }
    }

    private static boolean isInBoardPosition(int row, int col) {
        // Check if the position is inside the board
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
}
